package pl.msitko.xml

import pl.msitko.xml.entities.XmlDocument
import pl.msitko.xml.test.utils.{BaseSpec, Example}
import pl.msitko.xml.parsing.ParsingException
import pl.msitko.xml.printing.PrinterConfig

trait BasicSpec extends BaseSpec {
  def parseExample(example: Example): XmlDocument = parse(example.stringRepr)
  def parse(input: String): XmlDocument = parseEither(input).right.get
  def parseEither(input: String): Either[ParsingException, XmlDocument]
  def print(doc: XmlDocument)(implicit cfg: PrinterConfig): String
}
