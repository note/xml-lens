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
    cfg.indent match {
      // TODO: we should instead of this assume that it's minimized, but for now minimize is defined only in optics
      case _: IndentWith if text.text.forall(_.isWhitespace) => writer
      case _ => InternalMonoid[M].combine(writer, Escaper.escapeText(text.text))
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


