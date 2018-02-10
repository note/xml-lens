package net.michalsitko.xml.parsing

case class ParserConfig(replaceEntityReferences: Boolean)

object ParserConfig {
  val Default = ParserConfig(replaceEntityReferences = false)
}
