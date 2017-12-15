package net.michalsitko.xml.printing

case class PrinterConfig(indent: Indent)

sealed trait Indent
object Indent {
  case object Remain extends Indent
  case class IndentWith(singleIndent: String) extends Indent
}
