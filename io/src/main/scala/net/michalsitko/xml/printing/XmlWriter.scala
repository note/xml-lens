package net.michalsitko.xml.printing

import java.io.Writer
import javax.xml.stream.XMLOutputFactory

import net.michalsitko.xml.entities._

// all methods returns Unit as it's designed to work over some mutable Writer-like type (like java.io.Writer or OutputStream)
trait XmlWriter {
  def writeLabeled(elem: LabeledElement): Unit

  def writeText(text: Text): Unit

  def writeComment(comment: Comment): Unit

  def writeDtd(dtd: Dtd): Unit

  def writeEndElement(): Unit

  def writeProcessingInstruction(pi: ProcessingInstruction): Unit

  def writeCData(cdata: CData): Unit
}

//class PrettyXmlWriter (cfg: PrinterConfig) extends CommonWriter {
//  private var nestedLevel: Int = 0
//
//  // TODO: assumes XML version 1.0 and utf-8, make it works with different values (change in XmlParser required)
//  writeInvocation("1.0", "UTF-8")
//
//  val ident: Int => Unit = cfg.identWith match {
//    case Some(identWith) => identLevel =>
//      sw.writeCharacters(EOL)
//      for (i <- 0 until identLevel) {
//        sw.writeCharacters(identWith)
//      }
//    case None =>
//      _ => ()
//  }
//
//  def writeLabeled(elem: LabeledElement): Unit = {
//    ident(nestedLevel)
//
//    sw.writeStartElement(elem.label.prefix, elem.label.localName, elem.label.uri)
//    elem.element.namespaceDeclarations.foreach { ns =>
//      sw.writeNamespace(ns.prefix, ns.uri)
//    }
//    elem.element.attributes.foreach { attr =>
//      sw.writeAttribute(attr.key.prefix, attr.key.uri, attr.key.localName, attr.value)
//    }
//
//    nestedLevel += 1
//  }
//
//  def writeText(text: Text): Unit = {
//    if(cfg.identWith.isDefined && text.text.forall(_.isWhitespace)) {
//
//    } else {
//      sw.writeCharacters(text.text)
//    }
//  }
//
//
//  def writeEndElement(elem: LabeledElement): Unit = {
//    val hasChildren = elem.element.children.exists {
//      case el: LabeledElement =>
//        true
//      case _ =>
//        false
//    }
//    nestedLevel -= 1
//    if(hasChildren) {
//      ident(nestedLevel)
//    }
//    sw.writeEndElement()
//  }
//
//}

class JavaXmlWriter(output: Writer) extends XmlWriter {
  private val EOL = System.getProperty("line.separator")
  private val sw = XMLOutputFactory.newFactory().createXMLStreamWriter(output)

  private def writeInvocation(version: String, encoding: String) = {
    sw.writeStartDocument(encoding, version)
    sw.writeCharacters(EOL)
  }

  // TODO: assumes XML version 1.0 and utf-8, make it works with different values (change in XmlParser required)
  writeInvocation("1.0" ,"UTF-8")

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

  def writeEndElement(): Unit =
    sw.writeEndElement()

  def writeComment(comment: Comment): Unit =
    sw.writeComment(comment.comment)

  def writeDtd(dtd: Dtd): Unit =
    sw.writeDTD(dtd.text)

  def writeProcessingInstruction(pi: ProcessingInstruction): Unit =
    sw.writeProcessingInstruction(pi.target, pi.data)

  def writeCData(cdata: CData): Unit =
    sw.writeCData(cdata.text)
}

case class PrinterConfig(identWith: Option[String])
