package net.michalsitko.xml.printing

import net.michalsitko.xml.entities.{Attribute, Element, LabeledElement, NamespaceDeclaration}

trait ElementWriter extends Resolver {
  def writeElement[M : InternalMonoid](element: LabeledElement, level: Int)(writer: M): M

  def writeEndElement[M : InternalMonoid](element: LabeledElement, level: Int)(writer: M): M

  protected def writeElementBase(element: LabeledElement): String = {
    def singleNsStr(ns: NamespaceDeclaration): String =
      if(ns.prefix.isEmpty) {
        s"""xmlns="${ns.uri}""""
      } else {
        s"""xmlns:${ns.prefix}="${ns.uri}""""
      }

    def singleAttrStr(attr: Attribute): String =
      s"""${resolve(attr.key)}="${Escaper.escapeAttributeValue(attr.value)}""""

    val nsStr = {
      val tmp = element.element.namespaceDeclarations.map(singleNsStr).mkString(" ")
      if (tmp.nonEmpty) {
        " " + tmp
      } else {
        tmp
      }
    }
    val attrStr = {
      val tmp = element.element.attributes.map(singleAttrStr).mkString(" ")
      if (tmp.nonEmpty) {
        " " + tmp
      } else {
        tmp
      }
    }

    s"<${resolve(element.label)}$nsStr$attrStr>"
  }

  protected def writeEndElementBase(element: LabeledElement): String = {
    s"</${resolve(element.label)}>"
  }
}

object ElementWriter {
  def forConfig(cfg: PrinterConfig): ElementWriter =
    cfg.indent match {
      case Indent.Remain => new SimpleElementWriter
      case Indent.IndentWith(singleIndent) => new PrettyElementWriter(singleIndent)
    }
}

class SimpleElementWriter extends ElementWriter {
  override def writeElement[M: InternalMonoid](element: LabeledElement, level: Int)(writer: M) = {
    InternalMonoid[M].combine(writer, writeElementBase(element))
  }

  override def writeEndElement[M : InternalMonoid](element: LabeledElement, level: Int)(writer: M): M =
    InternalMonoid[M].combine(writer, writeEndElementBase(element))
}

class PrettyElementWriter(singleIndent: String) extends ElementWriter {
  val systemEol = System.getProperty("line.separator")

  override def writeElement[M: InternalMonoid](element: LabeledElement, level: Int)(writer: M) = {
    val indent = singleIndent * level
    val fullIndent =
      if(level > 0) {
        systemEol + indent
      } else {
        indent
      }

    val elStr = s"$fullIndent${writeElementBase(element)}"
    InternalMonoid[M].combine(writer, elStr)
  }

  override def writeEndElement[M : InternalMonoid](element: LabeledElement, level: Int)(writer: M): M = {
    val indent =
      if(hasChildren(element.element)) {
        systemEol + singleIndent * (level - 1)
      } else {
        ""
      }

    val endStr = s"$indent${writeEndElementBase(element)}"
    InternalMonoid[M].combine(writer, endStr)
  }

  private def hasChildren(element: Element): Boolean =
    element.children.exists {
      case el: LabeledElement =>
        true
      case _ =>
        false
    }
}
