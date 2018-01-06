package net.michalsitko.xml.printing

case class PrinterConfig(indent: Indent)

object PrinterConfig {
  val Default = PrinterConfig(Indent.Remain)
}

sealed trait Indent
object Indent {
  case object Remain extends Indent
  case class IndentWith(singleIndent: String) extends Indent
}
