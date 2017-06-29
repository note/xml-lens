package net.michalsitko.xml.printing

import net.michalsitko.xml.entities._

trait XmlWriter {
  def build: String

  def writeLabeled(elem: LabeledElement): Unit

  def writeText(text: Text): Unit

  def writeComment(comment: Comment): Unit

  def writeEndElement(elem: LabeledElement): Unit
}

abstract class CommonWriter extends XmlWriter {
  protected val sb: StringBuilder

  // TODO: assumes XML version 1.0 and utf-8, make it works with different values (change in XmlParser required)
  protected val xmlInvocation = """<?xml version="1.0" encoding="UTF-8"?>"""

  protected val EOL = System.getProperty("line.separator")

  def build: String =
    sb.toString

  def writeComment(comment: Comment): Unit = {
    sb.append("<!--")
    sb.append(comment.comment)
    sb.append("-->")
  }

  protected def writeElementLabel(resolvedName: ResolvedName): Unit = {
    sb.append("<")
    sb.append(resolvedNameToString(resolvedName))
  }

  protected def writeNamespaces(namespaces: Seq[NamespaceDeclaration]): Unit = {
    namespaces.foreach { ns =>
      if(ns.prefix.nonEmpty) {
        sb.append(s""" xmlns:${ns.prefix}="${ns.uri}"""")
      } else {
        sb.append(s""" xmlns="${ns.uri}"""")
      }
    }
  }

  protected def writeAttributes(attributes: Seq[Attribute]): Unit = {
    attributes.foreach { attr =>
      sb.append(s""" ${resolvedNameToString(attr.key)}="${attr.value}"""")
    }
  }

  protected def resolvedNameToString(r: ResolvedName): String = {
    if(r.prefix.isEmpty) {
      r.localName
    } else {
      r.prefix + ":" + r.localName
    }
  }
}

class PrettyXmlWriter (cfg: PrinterConfig) extends CommonWriter {
  val sb = new StringBuilder

  private var nestedLevel: Int = 0

  sb.append(xmlInvocation)

  val ident: Int => Unit = cfg.identWith match {
    case Some(identWith) => identLevel =>
      sb.append(EOL)
      for (i <- 0 until identLevel) {
        sb.append(identWith)
      }
    case None =>
      _ => ()
  }

  def writeLabeled(elem: LabeledElement): Unit = {
    ident(nestedLevel)
    writeElementLabel(elem.label)
    writeNamespaces(elem.element.namespaceDeclarations)
    writeAttributes(elem.element.attributes)
    sb.append(">")
    nestedLevel += 1
  }

  def writeText(text: Text): Unit = {
    if(cfg.identWith.isDefined && text.text.forall(_.isWhitespace)) {

    } else {
      sb.append(text.text)
    }
  }


  def writeEndElement(elem: LabeledElement): Unit = {
    val hasChildren = elem.element.children.exists {
      case el: LabeledElement =>
        true
      case _ =>
        false
    }
    nestedLevel -= 1
    if(hasChildren) {
      ident(nestedLevel)
    }
    sb.append(s"</${resolvedNameToString(elem.label)}>")
  }

}

class SimpleXmlWriter extends CommonWriter {
  val sb = new StringBuilder

  sb.append(xmlInvocation)
  sb.append(EOL)

  def writeLabeled(elem: LabeledElement): Unit = {
    writeElementLabel(elem.label)
    writeNamespaces(elem.element.namespaceDeclarations)
    writeAttributes(elem.element.attributes)
    sb.append(">")
  }

  def writeText(text: Text): Unit = {
    sb.append(text.text)
  }

  def writeEndElement(elem: LabeledElement): Unit = {
    sb.append(s"</${resolvedNameToString(elem.label)}>")
  }
}

case class PrinterConfig(identWith: Option[String])
