package net.michalsitko.xml.printing

import java.io.Writer
import javax.xml.stream.{XMLOutputFactory, XMLStreamWriter}
import net.michalsitko.xml.entities._

// all methods returns Unit as it's designed to work over some mutable Writer-like type (like java.io.Writer or OutputStream)
trait XmlWriter {
  def writeProlog(prolog: Prolog): Unit

  def writeLabeled(elem: LabeledElement): Unit

  def writeText(text: Text): Unit

  def writeComment(comment: Comment): Unit

  def writeDtd(doctypeDeclaration: DoctypeDeclaration): Unit

  def writeEndElement(elem: LabeledElement): Unit

  def writeProcessingInstruction(pi: ProcessingInstruction): Unit

  def writeCData(cdata: CData): Unit
}

abstract class CommonWriter extends XmlWriter {
  protected val sw: XMLStreamWriter
  protected val EOL = System.getProperty("line.separator")

  protected def writeInvocation(declaration: XmlDeclaration) = {
    declaration.encoding match {
      case Some(encoding) => sw.writeStartDocument(encoding, declaration.version)
      case None           => sw.writeStartDocument(declaration.version)
    }
    sw.writeCharacters(EOL)
  }

  protected def writeMisc(misc: Misc): Unit = misc match {
    case c: Comment                 => writeComment(c)
    case pi: ProcessingInstruction  => writeProcessingInstruction(pi)
  }

  def writeProlog(prolog: Prolog): Unit = {
    prolog.xmlDeclaration.foreach(writeInvocation)
    prolog.miscs.foreach(writeMisc)
    prolog.doctypeDeclaration.foreach {
      case (doctypeDecl, miscs) =>
        writeDtd(doctypeDecl)
        miscs.foreach(writeMisc)
    }
  }

  def writeComment(comment: Comment): Unit =
    sw.writeComment(comment.comment)

  def writeDtd(doctypeDeclaration: DoctypeDeclaration): Unit =
    sw.writeDTD(doctypeDeclaration.text)

  def writeProcessingInstruction(pi: ProcessingInstruction): Unit =
    sw.writeProcessingInstruction(pi.target, pi.data)

  def writeCData(cdata: CData): Unit =
    sw.writeCData(cdata.text)

}

class PrettyXmlWriter (output: Writer, cfg: PrinterConfig) extends CommonWriter {
  private var nestedLevel: Int = 0
  protected val sw = XMLOutputFactory.newFactory().createXMLStreamWriter(output)

  val ident: Int => Unit = cfg.identWith match {
    case Some(identWith) => identLevel =>
      for (i <- 0 until identLevel) {
        sw.writeCharacters(identWith)
      }
    case None =>
      _ => ()
  }

  def writeLabeled(elem: LabeledElement): Unit = {
    if(nestedLevel > 0) {
      sw.writeCharacters(EOL)
    }
    ident(nestedLevel)

    sw.writeStartElement(elem.label.prefix, elem.label.localName, elem.label.uri)
    elem.element.namespaceDeclarations.foreach { ns =>
      sw.writeNamespace(ns.prefix, ns.uri)
    }
    elem.element.attributes.foreach { attr =>
      sw.writeAttribute(attr.key.prefix, attr.key.uri, attr.key.localName, attr.value)
    }

    nestedLevel += 1
  }

  def writeText(text: Text): Unit = {
    if(cfg.identWith.isDefined && text.text.forall(_.isWhitespace)) {

    } else {
      sw.writeCharacters(text.text)
    }
  }

  def writeEndElement(elem: LabeledElement): Unit = {
    nestedLevel -= 1

    val hasChildren = elem.element.children.exists {
      case el: LabeledElement =>
        true
      case _ =>
        false
    }
    if(hasChildren) {
      sw.writeCharacters(EOL)
      ident(nestedLevel)
    }
    sw.writeEndElement()
  }

}

class JavaXmlWriter(output: Writer, cfg: PrinterConfig) extends CommonWriter {
  protected val sw = XMLOutputFactory.newFactory().createXMLStreamWriter(output)

  def writeLabeled(elem: LabeledElement): Unit = {
    sw.writeStartElement(elem.label.prefix, elem.label.localName, elem.label.uri)
    elem.element.namespaceDeclarations.foreach { ns =>
      sw.writeNamespace(ns.prefix, ns.uri)
    }
    elem.element.attributes.foreach { attr =>
      sw.writeAttribute(attr.key.prefix, attr.key.uri, attr.key.localName, attr.value)
    }
  }

  def writeText(text: Text): Unit =
    sw.writeCharacters(text.text)

  def writeEndElement(elem: LabeledElement): Unit =
    sw.writeEndElement()

}

// TODO: document and test the difference between identWith = None and identWith = Some("")
case class PrinterConfig(identWith: Option[String])
