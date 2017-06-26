package net.michalsitko.xml.parsing

import java.io.{ByteArrayInputStream, IOException, InputStream}
import java.nio.charset.StandardCharsets
import javax.xml.stream.XMLStreamConstants._
import javax.xml.stream.{XMLInputFactory, XMLStreamReader}

import net.michalsitko.xml.entities._

import scala.annotation.tailrec
import scala.collection.mutable.ArrayBuffer
import scala.util.Try

// TODO: create proper hierarchy of errors
case class ParsingException(message: String, cause: Throwable) extends Exception(message, cause)

private [parsing] class LabeledElementBuilder(val label: ResolvedName, val attributes: Seq[Attribute], var children: ArrayBuffer[Node], val namespaceDeclarations: Seq[NamespaceDeclaration]) {
  def addChild(node: Node): Unit = {
    children += (node)
  }

  def build: LabeledElement = {
    LabeledElement(label, Element(attributes, children.toList, namespaceDeclarations))
  }
}

object XmlParser {
  def parse(input: String): Either[ParsingException, LabeledElement] = {
    // TODO: is it ok to hardcode UTF 8?
    val stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    parse(stream)
  }

  def parse(inputStream: InputStream): Either[ParsingException, LabeledElement] = {
    import net.michalsitko.xml.parsing.utils.TryOps._

    Try(read(inputStream)).asEither.left.map(e => ParsingException(s"Cannot parse XML: ${e.getMessage}", e))
  }

  private def read(inputStream: InputStream): LabeledElement = {
    val xmlInFact = XMLInputFactory.newInstance()
    val reader = xmlInFact.createXMLStreamReader(inputStream)

    firstElement(reader) match {
      case Some(resolvedName) =>
        val nsDeclarations = getNamespaceDeclarations(reader)
        val attrs = getAttributes(reader)
        val root = new LabeledElementBuilder(resolvedName, attrs, ArrayBuffer.empty[Node], nsDeclarations)
        readNext(root, reader)
        root.build
      case None => // should not really happen - XMLStreamReader takes care of it
        throw new IOException("No root element in XML document")
    }
  }

  @tailrec
  def firstElement(reader: XMLStreamReader): Option[ResolvedName] = {
    if(reader.hasNext()) {
      if(reader.next() == START_ELEMENT) {
        Some(getName(reader))
      } else {
        firstElement(reader)
      }
    } else {
      None
    }
  }

  // TODO: this may be slow - check it with JMH after optimizations
  def readNext(parent: LabeledElementBuilder, reader: XMLStreamReader): Unit = {
    var elementStack = List.empty[LabeledElementBuilder]
    elementStack = parent :: elementStack

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
          elementStack.tail.headOption.map(_.addChild(current.build))
          elementStack = elementStack.tail

        case COMMENT =>
          val parent = elementStack.head
          val commentText = reader.getText()
          parent.addChild(Comment(commentText))

        case _ =>
          // ignore for now
      }
    }
  }

  private def getName(reader: XMLStreamReader): ResolvedName = {
    val prefix = reader.getPrefix()
    val uri = reader.getNamespaceURI()
    val localName = reader.getLocalName()

    ResolvedName(prefix, Option(uri), localName)
  }

  private def getNamespaceDeclarations(reader: XMLStreamReader): Seq[NamespaceDeclaration] =
    for {
      i       <- 0 until reader.getNamespaceCount
      prefix  = reader.getNamespacePrefix(i)
      uri     = reader.getNamespaceURI(i)
    } yield NamespaceDeclaration(Option(prefix), uri)

  private def getAttributes(reader: XMLStreamReader): Seq[Attribute] = {
    for {
      i         <- 0 until reader.getAttributeCount
      prefix    = reader.getAttributePrefix(i)
      namespace = reader.getAttributeNamespace(i)
      localName = reader.getAttributeLocalName(i)
      value     = reader.getAttributeValue(i)
      resolved  = ResolvedName(prefix, Option(namespace), localName)
    } yield Attribute(resolved, value)
  }
}
