package net.michalsitko.parsing

import java.io.{IOException, StringReader}
import javax.xml.stream.{XMLInputFactory, XMLStreamException, XMLStreamReader}
import javax.xml.stream.XMLStreamConstants._

import net.michalsitko.entities._

import scala.annotation.tailrec
import scala.util.Try

// TODO: create proper hierarchy of errors
sealed trait ParsingError
case object SomeParsingError extends ParsingError

object XmlParser {
  def parse(input: String): Either[ParsingError, Element] = {
    // IOException and XMLStreamException
    Try(read(input)).toEither.left.map(_ => SomeParsingError)
  }

  private def read(input: String): Element = {
    val xmlInFact = XMLInputFactory.newInstance()
    val reader = xmlInFact.createXMLStreamReader(new StringReader(input))

    firstElement(reader) match {
      case Some(resolvedName) =>
        val nsDeclarations = getNamespaceDeclarations(reader)
        val attrs = getAttributes(reader)
        readNext(Element(resolvedName, Details(attrs, Seq.empty, nsDeclarations)), reader)
      case None =>
        // TODO: think about it
        throw new IOException("no root element")
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

  // TODO: this is slow - check it with JMH after optimizations
  // TODO: non-tail recursion
  def readNext(parent: Element, reader: XMLStreamReader): Element = {
    if(reader.hasNext) {
      reader.next() match {
        case START_ELEMENT =>
          val nsDeclarations = getNamespaceDeclarations(reader)
          val attrs = getAttributes(reader)
          val label = getName(reader)
          val initialChild = Element(label, Details(attrs, Seq.empty, nsDeclarations))
          val child = readNext(initialChild, reader)
          val newChildren = parent.elementDetails.children :+ child
          val newParent = parent.copy(elementDetails = parent.elementDetails.copy(children = newChildren))
          readNext(newParent, reader)

        case CHARACTERS =>
          val start = reader.getTextStart
          val length = reader.getTextLength
          val text = new String(reader.getTextCharacters(), start, length)
          val newChildren = parent.elementDetails.children :+ Text(text)
          val newParent = parent.copy(elementDetails = parent.elementDetails.copy(children = newChildren))
          readNext(newParent, reader)

        case END_ELEMENT =>
          parent
      }
    } else {
      // TODO: think about sth else
      throw new IOException("missing END_ELEMENT")
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
      i <- 0 until reader.getNamespaceCount
      prefix = reader.getNamespacePrefix(i)
      uri = reader.getNamespaceURI(i)
    } yield NamespaceDeclaration(Option(prefix), uri)

  private def getAttributes(reader: XMLStreamReader): Seq[Attribute] = {
    for {
      i <- 0 until reader.getAttributeCount
      prefix = reader.getAttributePrefix(i)
      namespace = reader.getAttributeNamespace(i)
      localName = reader.getAttributeLocalName(i)
      value = reader.getAttributeValue(i)
    } yield Attribute(prefix, Option(namespace), localName, value)
  }
}
