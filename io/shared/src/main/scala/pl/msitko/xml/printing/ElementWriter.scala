package pl.msitko.xml.printing

import pl.msitko.xml.entities.{Attribute, Element, LabeledElement, NamespaceDeclaration}
import pl.msitko.xml.printing.Syntax._

trait ElementWriter extends Resolver {
  def writeElement[M : InternalMonoid](element: LabeledElement, level: Int)(writer: M): M

  def writeEndElement[M : InternalMonoid](element: LabeledElement, level: Int)(writer: M): M

  protected def writeElementBase[M : InternalMonoid](element: LabeledElement)(writer: M): M = {
    def singleNsStr(ns: NamespaceDeclaration)(writer: M): M =
      if(ns.prefix.isEmpty) {
        val t1 = writer
          .combine("xmlns=\"")
        Escaper.escapeAttributeValue(ns.uri)(t1).combine("\"")
      } else {
        val t1 = writer
          .combine("xmlns:")
          .combine(ns.prefix)
          .combine("=\"")
        Escaper.escapeAttributeValue(ns.uri)(t1).combine("\"")
      }

    def singleAttrStr(attr: Attribute)(writer: M): M = {
      val t1 = writer
        .combine(resolve(attr.key))
        .combine("=\"")
      Escaper.escapeAttributeValue(attr.value)(t1).combine("\"")
    }

    val tmp = writer
      .combine("<")
      .combine(resolve(element.label))

    val withNs =
      element.element.namespaceDeclarations.foldLeft(tmp) { (acc, ns) =>
        val tmp = acc.combine(" ")
        singleNsStr(ns)(tmp)
      }

    val w =
      element.element.attributes.foldLeft(withNs) { (acc, attr) =>
        val tmp = acc.combine(" ")
        singleAttrStr(attr)(tmp)
      }

    w.combine(">")
  }

  protected def writeEndElementBase[M : InternalMonoid](element: LabeledElement)(writer: M): M = {
    writer
      .combine("</")
      .combine(resolve(element.label))
      .combine(">")
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
    writeElementBase(element)(writer)
  }

  override def writeEndElement[M : InternalMonoid](element: LabeledElement, level: Int)(writer: M): M =
    writeEndElementBase(element)(writer)
}

class PrettyElementWriter(singleIndent: String) extends ElementWriter {
  val systemEol = System.getProperty("line.separator")

  override def writeElement[M: InternalMonoid](element: LabeledElement, level: Int)(writer: M): M = {
    val indent = singleIndent * level
    val fullIndent =
      if(level > 0) {
        systemEol + indent
      } else {
        indent
      }

    val updated = writer.combine(fullIndent)
    writeElementBase(element)(updated)
  }

  override def writeEndElement[M : InternalMonoid](element: LabeledElement, level: Int)(writer: M): M = {
    val indent =
      if(hasChildren(element.element)) {
        systemEol + singleIndent * (level - 1)
      } else {
        ""
      }

    val updated = writer.combine(indent)
    writeEndElementBase(element)(updated)
  }

  private def hasChildren(element: Element): Boolean =
    element.children.exists {
      case el: LabeledElement =>
        true
      case _ =>
        false
    }
}
