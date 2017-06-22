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
case class ParsingException(message: String, cause: Throwable) extends Exception(message, cause)

private [parsing] class LabeledElementBuilder(val label: ResolvedName, val attributes: Seq[Attribute], var children: Vector[Node], val namespaceDeclarations: Seq[NamespaceDeclaration]) {
  def addChild(node: Node): Unit = {
    children = children :+ node
  }

  def build: LabeledElement = {
    LabeledElement(label, Element(attributes, children, namespaceDeclarations))
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
        readNext(new LabeledElementBuilder(resolvedName, attrs, Vector.empty, nsDeclarations), reader).run.build
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
  def readNext(parent: LabeledElementBuilder, reader: XMLStreamReader): Trampoline[LabeledElementBuilder] = {
    if(reader.hasNext) {
      reader.next() match {
        case START_ELEMENT =>
          val nsDeclarations = getNamespaceDeclarations(reader)
          val attrs = getAttributes(reader)
          val label = getName(reader)
          val initialChild = new LabeledElementBuilder(label, attrs, Vector.empty, nsDeclarations)
          for {
            child <- Trampoline.suspend(readNext(initialChild, reader))
            _ = parent.addChild(child.build)
            next <- Trampoline.suspend(readNext(parent, reader))
          } yield next

        case CHARACTERS =>
          val text = reader.getText
          parent.addChild(Text(text))
          Trampoline.suspend(readNext(parent, reader))

        case END_ELEMENT =>
          Trampoline.done(parent)

        case COMMENT =>
          val commentText = reader.getText()
          parent.addChild(Comment(commentText))
          Trampoline.suspend(readNext(parent, reader))
      }
    } else {
      Trampoline.done(parent)
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
