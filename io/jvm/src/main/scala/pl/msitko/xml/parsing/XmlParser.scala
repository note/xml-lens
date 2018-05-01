package pl.msitko.xml.parsing

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.{Charset, StandardCharsets}
import java.nio.file.{Files, Path}
import javax.xml.stream.XMLStreamConstants._
import javax.xml.stream.{XMLInputFactory, XMLResolver, XMLStreamReader}

import pl.msitko.xml.entities._
import pl.msitko.xml.parsing.utils.TryOps._

import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}
import scala.util.control.NonFatal

private [parsing] class LabeledElementBuilder(label: ResolvedName, attributes: Seq[Attribute], namespaceDeclarations: Seq[NamespaceDeclaration]) {
  val children: ArrayBuffer[Node] = ArrayBuffer.empty[Node]

   def addChild(node: Node): Unit = {
    children += (node)
  }

  def build: LabeledElement = {
    LabeledElement(label, Element(attributes, children.toList, namespaceDeclarations))
  }
}

// TODO: document it
private [parsing] object BlankingResolver extends XMLResolver {
  override def resolveEntity(publicID: String, systemID: String, baseURI: String, namespace: String): AnyRef = {
    new ByteArrayInputStream("".getBytes)
  }
}

final case class ParserConfig(replaceEntityReferences: Boolean)

object ParserConfig {
  val Default = ParserConfig(replaceEntityReferences = false)
}

object XmlParser {

  def parse(input: String, charset: Charset = StandardCharsets.UTF_8)(implicit config: ParserConfig = ParserConfig.Default): Either[ParsingException, XmlDocument] = {
    val stream = new ByteArrayInputStream(input.getBytes(charset))
    parseStream(stream)
  }

  def parseStream(inputStream: InputStream)(implicit config: ParserConfig = ParserConfig.Default): Either[ParsingException, XmlDocument] =
    Try(read(inputStream, config)).asEither.left.map {
      case e: ParsingException  => e
      case NonFatal(e)          => ParsingException(s"Cannot parse XML: ${e.getMessage}", e)
    }

  def parsePath(path: Path)(implicit config: ParserConfig = ParserConfig.Default): Either[XmlException, XmlDocument] =
    Try(Files.newInputStream(path)) match {
      case Success(is) => parseStream(is)
      case Failure(e)  => Left(PathException(s"Error occured when opening $path: ${e.getMessage}", e))
    }

  private def read(inputStream: InputStream, config: ParserConfig): XmlDocument = {
    val reader = {
      val xmlFactory = XMLInputFactory.newInstance()

      // see more at https://docs.oracle.com/javase/7/docs/api/javax/xml/stream/XMLInputFactory.html
      // TODO: do we really need to set those properties? Understand them better and either remove or document it better here
      xmlFactory.setProperty("javax.xml.stream.isReplacingEntityReferences", config.replaceEntityReferences)
      xmlFactory.setProperty("javax.xml.stream.isValidating", false)
      xmlFactory.setXMLResolver(BlankingResolver)

      // https://stackoverflow.com/questions/8591644/need-a-cdata-event-notifying-stax-parser-for-java
      xmlFactory.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", true)
      xmlFactory.createXMLStreamReader(inputStream)
    }

    readNext(reader)
  }

  private def readNext(reader: XMLStreamReader): XmlDocument = {
    def newElementBuilder(): LabeledElementBuilder = {
      val nsDeclarations = getNamespaceDeclarations(reader)
      val attrs = getAttributes(reader)
      val label = getName(reader)
      new LabeledElementBuilder(label, attrs, nsDeclarations)
    }

    def readUntilRootElement: (Prolog, Option[LabeledElementBuilder]) = {
      var curr = if (reader.hasNext) reader.next else null

      val xmlDeclaration = getDeclaration(reader)

      var f1 = Vector.empty[Misc]
      var f2 = Vector.empty[Misc]
      var doctypeDecl = Option.empty[DoctypeDeclaration]

      // TODO: test it thoroughly
      while(curr != null && curr != START_ELEMENT) {
        curr match {
          case COMMENT =>
            val comment = Comment(reader.getText())
            doctypeDecl match {
              case None =>
                f1 = f1 :+ comment
              case Some(_) =>
                f2 = f2 :+ comment
            }

          case PROCESSING_INSTRUCTION =>
            val pi = ProcessingInstruction(reader.getPITarget(), reader.getPIData())
            doctypeDecl match {
              case None =>
                f1 = f1 :+ pi
              case Some(_) =>
                f2 = f2 :+ pi
            }

          case DTD =>
            val decl = DoctypeDeclaration(reader.getText())
            doctypeDecl = Some(decl)
        }
        curr = if (reader.hasNext) reader.next else null
      }

      val prolog = Prolog(xmlDeclaration, f1, doctypeDecl.map(decl => (decl, f2)))

      if (curr == null) {
        (prolog, None)
      } else {
        (prolog, Some(newElementBuilder()))
      }
    }

    readUntilRootElement match {
      case (prolog, Some(root)) =>
        var elementStack = List(root)

        def addToStack(node: => Node): Unit = {
          elementStack.head.addChild(node)
        }

        while (reader.hasNext) {
          reader.next() match {
            case START_ELEMENT =>
              elementStack = newElementBuilder() :: elementStack

            case END_ELEMENT =>
              val current = elementStack.head
              elementStack.tail.headOption.foreach(_.addChild(current.build))
              elementStack = elementStack.tail

            case PROCESSING_INSTRUCTION =>
              addToStack {
                ProcessingInstruction(reader.getPITarget(), reader.getPIData())
              }

            case CHARACTERS =>
              addToStack {
                Text(reader.getText())
              }

            case COMMENT =>
              addToStack {
                Comment(reader.getText())
              }

            case CDATA =>
              addToStack {
                CData(reader.getText())
              }

            case ENTITY_REFERENCE =>
              addToStack {
                EntityReference(reader.getLocalName(), reader.getText())
              }

            case SPACE | // should not happen as our XmlProcessor is in non-validating mode see more at https://docs.oracle.com/cd/E17802_01/webservices/webservices/docs/1.5/api/javax/xml/stream/events/Characters.html
                 START_DOCUMENT | END_DOCUMENT | ATTRIBUTE | NAMESPACE | ENTITY_DECLARATION | // handled on different level (via calling methods on element)
                 DTD | // should not appear within root element
                 NOTATION_DECLARATION => // this element of XML is ignored for now
            // ignore
          }
        }

        XmlDocument(prolog, root.build)

      case (prolog, None) =>
        throw new ParsingException("no root element found", null)
    }
  }

  private def getDeclaration(reader: XMLStreamReader) = {
    Option(reader.getVersion).map { version =>
      XmlDeclaration(version, Option(reader.getCharacterEncodingScheme))
    }
  }

  private def getName(reader: XMLStreamReader) = {
    val prefix = reader.getPrefix()
    val uri = getString(reader.getNamespaceURI())
    val localName = reader.getLocalName()

    ResolvedName(prefix, uri, localName)
  }

  private def getNamespaceDeclarations(reader: XMLStreamReader) =
    for {
      i       <- 0 until reader.getNamespaceCount
      prefix  = getString(reader.getNamespacePrefix(i))
      uri     = reader.getNamespaceURI(i)
    } yield NamespaceDeclaration(prefix, getString(uri))

  private def getAttributes(reader: XMLStreamReader) = {
    for {
      i         <- 0 until reader.getAttributeCount
      prefix    = reader.getAttributePrefix(i)
      namespace = getString(reader.getAttributeNamespace(i))
      localName = reader.getAttributeLocalName(i)
      value     = reader.getAttributeValue(i)
      resolved  = ResolvedName(prefix, namespace, localName)
    } yield Attribute(resolved, value)
  }

  private def getString(input: String) = {
    if(input != null) {
      input
    } else {
      ""
    }
  }
}
