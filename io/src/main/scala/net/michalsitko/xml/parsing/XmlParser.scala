package net.michalsitko.xml.parsing

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.{Charset, StandardCharsets}
import javax.xml.stream.XMLStreamConstants._
import javax.xml.stream.{XMLInputFactory, XMLResolver, XMLStreamReader}
import net.michalsitko.xml.entities._

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

// TODO: create proper hierarchy of errors
case class ParsingException(message: String, cause: Throwable) extends Exception(message, cause)

private [parsing] class LabeledElementBuilder(label: ResolvedName, attributes: Seq[Attribute], children: ArrayBuffer[Node], namespaceDeclarations: Seq[NamespaceDeclaration]) {
   def addChild(node: Node): Unit = {
    children += (node)
  }

  def build: LabeledElement = {
    LabeledElement(label, Element(attributes, children.toList, namespaceDeclarations))
  }
}

private [parsing] object BlankingResolver extends XMLResolver {
  override def resolveEntity(publicID: String, systemID: String, baseURI: String, namespace: String): AnyRef = {
    new ByteArrayInputStream("".getBytes)
  }
}

case class ParserConfig(replaceEntityReferences: Boolean)

object XmlParser {
  import net.michalsitko.xml.parsing.utils.TryOps._

  val DefaultParserConfig = ParserConfig(replaceEntityReferences = false)

  // TODO: think about making return type Either[ParsingException, Node]. Current version use an (unneccessary?) assumption
  def parse(input: String, charset: Charset = StandardCharsets.UTF_8)(implicit config: ParserConfig = DefaultParserConfig): Either[ParsingException, XmlDocument] = {
    val stream = new ByteArrayInputStream(input.getBytes(charset))
    parseStream(stream)
  }

  def parseStream(inputStream: InputStream)(implicit config: ParserConfig = DefaultParserConfig): Either[ParsingException, XmlDocument] =
    Try(read(inputStream, config)).asEither.left.map(e => ParsingException(s"Cannot parse XML: ${e.getMessage}", e))

  private def read(inputStream: InputStream, config: ParserConfig): XmlDocument = {
    val reader = {
      val xmlInFact = XMLInputFactory.newInstance()

      // TODO: do we really need to set those properties? Understand them better and either remove or document it better here
      xmlInFact.setProperty("javax.xml.stream.isReplacingEntityReferences", false)
      xmlInFact.setXMLResolver(BlankingResolver)

      // https://stackoverflow.com/questions/8591644/need-a-cdata-event-notifying-stax-parser-for-java
      xmlInFact.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", true)
      xmlInFact.createXMLStreamReader(inputStream)
    }

    readNext(reader)
  }

  private def readNext(reader: XMLStreamReader): XmlDocument = {
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
        // TODO: remove duplication (1)
        val nsDeclarations = getNamespaceDeclarations(reader)
        val attrs = getAttributes(reader)
        val label = getName(reader)
        (prolog, Some(new LabeledElementBuilder(label, attrs, ArrayBuffer.empty[Node], nsDeclarations)))
      }
    }

    readUntilRootElement match {
      case (prolog, Some(root)) =>
        var elementStack = List(root)

        while (reader.hasNext) {
          reader.next() match {
            case START_ELEMENT =>
              // TODO: remove duplication (2)
              val nsDeclarations = getNamespaceDeclarations(reader)
              val attrs = getAttributes(reader)
              val label = getName(reader)
              val newChild = new LabeledElementBuilder(label, attrs, ArrayBuffer.empty[Node], nsDeclarations)
              elementStack = newChild :: elementStack

            case CHARACTERS =>
              val parent = elementStack.head
              val text = reader.getText
              parent.addChild(Text(text))

            case END_ELEMENT =>
              val current = elementStack.head
              elementStack.tail.headOption.foreach(_.addChild(current.build))
              elementStack = elementStack.tail

            case COMMENT =>
              val parent = elementStack.head
              val commentText = reader.getText()
              parent.addChild(Comment(commentText))

            case PROCESSING_INSTRUCTION =>
              val parent = elementStack.head
              val pi = ProcessingInstruction(reader.getPITarget(), reader.getPIData())
              parent.addChild(pi)

            case CDATA =>
              val parent = elementStack.head
              val commentText = reader.getText()
              parent.addChild(CData(commentText))

            case ENTITY_REFERENCE =>
              val parent = elementStack.head
              val ref = EntityReference(reader.getLocalName(), reader.getText())
              parent.addChild(ref)

            case _ =>
            // TODO: ignore for now
          }
        }

        XmlDocument(prolog, root.build)

      case (prolog, None) =>
        throw new RuntimeException("no root element found") // TODO: change it, generally think about error handling
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
