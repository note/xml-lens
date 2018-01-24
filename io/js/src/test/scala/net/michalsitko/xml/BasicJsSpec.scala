package net.michalsitko.xml

import net.michalsitko.xml.entities.XmlDocument
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.PrinterConfig

trait BasicJsSpec extends BasicSpec {
  override def parseEither(input: String) = XmlParser.parse(input)
  override def print(doc: XmlDocument)(implicit cfg: PrinterConfig) = ???
}
