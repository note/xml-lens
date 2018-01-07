package net.michalsitko.xml

import net.michalsitko.xml.entities.XmlDocument
import net.michalsitko.xml.parsing.{ParsingException, XmlParser}
import net.michalsitko.xml.printing.{PrinterConfig, XmlPrinter}
import net.michalsitko.xml.test.utils.Example

trait BasicJvmSpec extends BasicSpec {
  def parseExample(example: Example): XmlDocument = XmlParser.parse(example.stringRepr).right.get
  def parse(input: String): XmlDocument = XmlParser.parse(input).right.get
  def parseEither(input: String): Either[ParsingException, XmlDocument] = XmlParser.parse(input)
  def print(doc: XmlDocument)(implicit cfg: PrinterConfig): String = XmlPrinter.print(doc)
}
