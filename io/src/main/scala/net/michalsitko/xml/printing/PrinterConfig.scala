package net.michalsitko.xml.printing

// TODO: document and test the difference between identWith = None and identWith = Some("")
case class PrinterConfig(indent: Indent)

sealed trait Indent
object Indent {
  case object Remain extends Indent
  case class IndentWith(singleIndent: String) extends Indent
}
