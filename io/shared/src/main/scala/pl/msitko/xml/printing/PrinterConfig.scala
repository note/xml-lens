package pl.msitko.xml.printing

final case class PrinterConfig(indent: Indent,
                               eolAfterXmlDecl: Boolean // controls whether an end of line should be included after XML Declaration
                              )

object PrinterConfig {
  val Default = PrinterConfig(Indent.Remain, true)
}

sealed trait Indent

object Indent {

  /** Prints document "as is" - XmlPrinter will not add any extra indentation
    *
    * Two most probable use cases of that value:
    * - when you want to preserve original formatting
    * - when you want to print minimized XML - call `NodeOps.minimized` before calling XmlPrinter.print and use Remain
    *
    */
  case object Remain extends Indent

  /** Causes each element to start in new line and intended one level more than its parent.
    *
    * @param singleIndent
    */
  final case class IndentWith(singleIndent: String) extends Indent
}
