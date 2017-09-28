package net.michalsitko.xml.parsing

import java.io.{ByteArrayInputStream, InputStream}
import java.nio.charset.{Charset, StandardCharsets}
import javax.xml.stream.XMLStreamConstants._
import javax.xml.stream.{XMLInputFactory, XMLResolver, XMLStreamReader}

import net.michalsitko.xml.XmlDeclaration
import net.michalsitko.xml.entities._

import scala.collection.mutable.ArrayBuffer
import scala.util.Try

// TODO: create proper hierarchy of errors
case class ParsingException(message: String, cause: Throwable) extends Exception(message, cause)

private [parsing] trait NodeBuilder {
  def addChild(node: Node): Unit
}

private [parsing] class TopNodesBuilder extends NodeBuilder {
  private val children = ArrayBuffer.empty[Node]

  def addChild(node: Node): Unit = {
    children += node
  }

  def build: Seq[Node] =
    children.toList
}

// TODO: those vals and vars on constructor level are unneccessary
private [parsing] class LabeledElementBuilder(val label: ResolvedName, val attributes: Seq[Attribute], var children: ArrayBuffer[Node], val namespaceDeclarations: Seq[NamespaceDeclaration]) extends NodeBuilder {
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

object XmlParser {
  // TODO: think about making return type Either[ParsingException, Node]. Current version use an (unneccessary?) assumption
  def parse(input: String, charset: Charset = StandardCharsets.UTF_8): Either[ParsingException, Seq[Node]] = {
    val stream = new ByteArrayInputStream(input.getBytes(charset))
    parse(stream)
  }

  def parseWithDeclaration(input: String, charset: Charset = StandardCharsets.UTF_8): Either[ParsingException, (Option[XmlDeclaration], Seq[Node])] = {
    val stream = new ByteArrayInputStream(input.getBytes(charset))
    parseWithDeclaration(stream)
  }

  def parse(inputStream: InputStream): Either[ParsingException, Seq[Node]] = {
    import net.michalsitko.xml.parsing.utils.TryOps._

    Try(read(inputStream)).map(_._2).asEither.left.map(e => ParsingException(s"Cannot parse XML: ${e.getMessage}", e))
  }

  def parseWithDeclaration(inputStream: InputStream): Either[ParsingException, (Option[XmlDeclaration], Seq[Node])] = {
    import net.michalsitko.xml.parsing.utils.TryOps._

    Try(read(inputStream)).asEither.left.map(e => ParsingException(s"Cannot parse XML: ${e.getMessage}", e))
  }

  private def read(inputStream: InputStream) = {
    val xmlInFact = XMLInputFactory.newInstance()
    xmlInFact.setXMLResolver(BlankingResolver)

    // https://stackoverflow.com/questions/8591644/need-a-cdata-event-notifying-stax-parser-for-java
    xmlInFact.setProperty("http://java.sun.com/xml/stream/properties/report-cdata-event", true)
    val reader = xmlInFact.createXMLStreamReader(inputStream)

    val xmlDeclaration = getDeclaration(reader)
    val topNodesBuilder = new TopNodesBuilder()
    readNext(topNodesBuilder, reader)

    (xmlDeclaration, topNodesBuilder.build)
  }

  private def readNext(root: NodeBuilder, reader: XMLStreamReader) = {
    var elementStack = List.empty[LabeledElementBuilder]

    while(reader.hasNext) {
      reader.next() match {
        case START_ELEMENT =>
          val nsDeclarations = getNamespaceDeclarations(reader)
          val attrs = getAttributes(reader)
          val label = getName(reader)
          val initialChild = new LabeledElementBuilder(label, attrs, ArrayBuffer.empty[Node], nsDeclarations)
          elementStack = initialChild :: elementStack

        case CHARACTERS =>
          val parent = elementStack.head
          val text = reader.getText
          parent.addChild(Text(text))

        case END_ELEMENT =>
          val current = elementStack.head
          elementStack.tail.headOption.getOrElse(root).addChild(current.build)
          elementStack = elementStack.tail

        case COMMENT =>
          // `headOption` because COMMENT can be top level Node
          val parent = elementStack.headOption.getOrElse(root)
          val commentText = reader.getText()
          parent.addChild(Comment(commentText))

        case PROCESSING_INSTRUCTION =>
          val parent = elementStack.headOption.getOrElse(root)
          val pi = ProcessingInstruction(reader.getPITarget(), reader.getPIData())
          parent.addChild(pi)

        case DTD =>
          val parent = elementStack.headOption.getOrElse(root)
          val commentText = reader.getText()
          parent.addChild(Dtd(commentText))

        case CDATA =>
          val parent = elementStack.head
          val commentText = reader.getText()
          parent.addChild(CData(commentText))

        case _ =>
          // ignore for now
      }
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
