package net.michalsitko.xml.printing

import net.michalsitko.xml.entities._

trait CommonXmlPrinter {
  def print(doc: XmlDocument)(implicit cfg: PrinterConfig = PrinterConfig.Default): String
}

object CommonXmlWriter {
  def writeProlog[M : InternalMonoid](prolog: Prolog, eolAfterXmlDecl: Boolean)(writer: M): M = {
    def xmlDeclStr(decl: XmlDeclaration): String = {
      val encStr = decl.encoding.map(enc => s""" encoding="$enc"""").getOrElse("")
      val eol = if(eolAfterXmlDecl) System.getProperty("line.separator") else ""
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

  def writeElement[M : InternalMonoid](element: LabeledElement)(writer: M): M = {
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
    val elementStr = s"<${resolve(element.label)}$nsStr$attrStr>"
    InternalMonoid[M].combine(writer, elementStr)
  }

  def writeEndElement[M : InternalMonoid](name: ResolvedName)(writer: M): M = {
    val endStr = s"</${resolve(name)}>"
    InternalMonoid[M].combine(writer, endStr)
  }

  def writeText[M : InternalMonoid](text: Text)(writer: M): M =
    InternalMonoid[M].combine(writer, text.text)

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

  private def resolve(name: ResolvedName): String =
    if (name.hasPrefix) {
      name.prefix + ":" + name.localName
    } else {
      name.localName
    }

}

// just not to make io module depending on cats/scalaz
private [printing] trait InternalMonoid [T] {
  def combine(a: T, b: String): T
  def zero: T
}

private [printing] object InternalMonoid {
  def apply[T : InternalMonoid]: InternalMonoid[T] = implicitly[InternalMonoid[T]]

  implicit val stringMonoidInstance = new InternalMonoid[String] {
    override def combine(a: String, b: String) = a + b

    override def zero = ""
  }
}



