package net.michalsitko.xml.parsing

import java.io.{ByteArrayInputStream, IOException, InputStream}
import java.nio.charset.StandardCharsets
import javax.xml.stream.XMLStreamConstants._
import javax.xml.stream.{XMLInputFactory, XMLStreamReader}

import net.michalsitko.xml.entities._

import scala.annotation.tailrec
import scala.util.Try
import scalaz.Free.Trampoline
import scalaz.Trampoline

// TODO: create proper hierarchy of errors
case class ParsingError(exception: Throwable)
//case object SomeParsingError extends ParsingError

object XmlParser {
  def parse(input: String): Either[ParsingError, LabeledElement] = {
    // TODO: is it ok to hardcode UTF 8?
    val stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8))
    parse(stream)
  }

  def parse(inputStream: InputStream): Either[ParsingError, LabeledElement] = {
    import net.michalsitko.xml.parsing.utils.TryOps._

    // IOException and XMLStreamException
    Try(read(inputStream)).asEither.left.map(e => ParsingError(e))
  }

  private def read(inputStream: InputStream): LabeledElement = {
    val xmlInFact = XMLInputFactory.newInstance()
    val reader = xmlInFact.createXMLStreamReader(inputStream)

    firstElement(reader) match {
      case Some(resolvedName) =>
        val nsDeclarations = getNamespaceDeclarations(reader)
        val attrs = getAttributes(reader)
        readNext(LabeledElement(resolvedName, Element(attrs, Seq.empty, nsDeclarations)), reader).run
      case None =>
        // TODO: think about it
        throw new IOException("bazinga: no root element")
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
  def readNext(parent: LabeledElement, reader: XMLStreamReader): Trampoline[LabeledElement] = {
    if(reader.hasNext) {
      reader.next() match {
        case START_ELEMENT =>
          val nsDeclarations = getNamespaceDeclarations(reader)
          val attrs = getAttributes(reader)
          val label = getName(reader)
          val initialChild = LabeledElement(label, Element(attrs, Seq.empty, nsDeclarations))
          for {
            child <- Trampoline.suspend(readNext(initialChild, reader))
            newChildren = parent.element.children :+ child
            newParent = parent.copy(element = parent.element.copy(children = newChildren))
            next <- Trampoline.suspend(readNext(newParent, reader))
          } yield next

        case CHARACTERS =>
          val start = reader.getTextStart
          val length = reader.getTextLength
          val text = new String(reader.getTextCharacters(), start, length)
          val newChildren = parent.element.children :+ Text(text)
          val newParent = parent.copy(element = parent.element.copy(children = newChildren))
          Trampoline.suspend(readNext(newParent, reader))

        case END_ELEMENT =>
          Trampoline.done(parent)

        case COMMENT =>
          val commentText = reader.getText()
          val newChildren = parent.element.children :+ Comment(commentText)
          val newParent = parent.copy(element = parent.element.copy(children = newChildren))
          Trampoline.suspend(readNext(newParent, reader))
      }
    } else {
      // TODO: think about sth else
      throw new IOException("bazinga: missing END_ELEMENT")
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
      resolved = ResolvedName(prefix, Option(namespace), localName)
    } yield Attribute(resolved, value)
  }
}
