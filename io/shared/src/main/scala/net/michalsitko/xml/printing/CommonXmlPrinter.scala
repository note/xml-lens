package net.michalsitko.xml.printing

import net.michalsitko.xml.entities._
import net.michalsitko.xml.printing.Indent.IndentWith

trait CommonXmlPrinter {
  def print(doc: XmlDocument)(implicit cfg: PrinterConfig = PrinterConfig.Default): String
}

object CommonXmlWriter extends Resolver {
  val systemEol = System.getProperty("line.separator")

  def writeProlog[M : InternalMonoid](prolog: Prolog, eolAfterXmlDecl: Boolean)(writer: M): M = {
    def xmlDeclStr(decl: XmlDeclaration): String = {
      val encStr = decl.encoding.map(enc => s""" encoding="$enc"""").getOrElse("")
      val eol = if(eolAfterXmlDecl) systemEol else ""
      s"""<?xml version="${decl.version}"$encStr?>$eol"""
    }

    def doctypeDeclStr(doctypeDeclaration: DoctypeDeclaration): String = {
      doctypeDeclaration.text
    }

    val declarationStr = prolog.xmlDeclaration.map { decl =>
      xmlDeclStr(decl)
    }.getOrElse("")

    val miscs = prolog.miscs.map(writeMisc).mkString("")
    val doctype = prolog.doctypeDeclaration.map { decl =>
      val s = doctypeDeclStr(decl._1)
      val miscs = prolog.miscs.map(writeMisc).mkString("")
      s + miscs
    }.getOrElse("")

    InternalMonoid[M].combine(writer, declarationStr + miscs + doctype)
  }

  def writeMisc(misc: Misc): String = misc match {
    case c: Comment =>
      commentString(c)
    case pi: ProcessingInstruction =>
      processingInstructionString(pi)
  }

  def writeText[M : InternalMonoid](text: Text)(writer: M, cfg: PrinterConfig): M = {
    // TODO: we should instead of this assume that it's minimized, but for now minimize is defined only in optics
    cfg.indent match {
      case _: IndentWith if text.text.forall(_.isWhitespace) => writer
      case _: IndentWith => InternalMonoid[M].combine(writer, text.text)
      case _ => InternalMonoid[M].combine(writer, text.text)
    }
  }

  def processingInstructionString(pi: ProcessingInstruction): String =
    s"<?${pi.target} ${pi.data}?>"

  def writeProcessingInstruction[M : InternalMonoid](node: ProcessingInstruction)(writer: M): M =
    InternalMonoid[M].combine(writer, processingInstructionString(node))

  // TDSect according to https://www.w3.org/TR/xml/#NT-CDSect
  def writeCData[M : InternalMonoid](node: CData)(writer: M): M = {
    val cdataStr = s"<![CDATA[${node.text}]]>"
    InternalMonoid[M].combine(writer, cdataStr)
  }

  def writeEntityReference[M : InternalMonoid](node: EntityReference)(writer: M): M = {
    val entityStr = s"&${node.name};"
    InternalMonoid[M].combine(writer, entityStr)
  }

  def commentString(comment: Comment) = s"<!--${comment.comment}-->"

  // Comment according to https://www.w3.org/TR/xml/#NT-Comment
  def writeComment[M : InternalMonoid](node: Comment)(writer: M): M =
    InternalMonoid[M].combine(writer, commentString(node))

}

trait Resolver {
  def resolve(name: ResolvedName): String =
    if (name.hasPrefix) {
      name.prefix + ":" + name.localName
    } else {
      name.localName
    }
}

// just not to make io module depending on cats/scalaz
trait InternalMonoid [T] {
  def combine(a: T, b: String): T
  def zero: T
}

object InternalMonoid {
  def apply[T : InternalMonoid]: InternalMonoid[T] = implicitly[InternalMonoid[T]]

  implicit val stringMonoidInstance = new InternalMonoid[String] {
    override def combine(a: String, b: String) = a + b

    override def zero = ""
  }
}

//trait Indenter {
//  def intend(level: Int): String
//}
//
//object NoIndenter extends Indenter {
//  def intend(level: Int): String = ""
//}
//
//class LevelIndenter(singleIntend: String) extends Indenter {
//  def intend(level: Int): String = singleIntend * level
//}

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
      s"""${resolve(attr.key)}="${attr.value}""""

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
//    val eol =
//      if(hasChildren(element.element)) {
//        systemEol
//      } else {
//        ""
//      }
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

