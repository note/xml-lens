package net.michalsitko.xml

import net.michalsitko.xml.entities.XmlDocument
import net.michalsitko.xml.parsing.ParsingException
import net.michalsitko.xml.printing.PrinterConfig
import net.michalsitko.xml.test.utils.{BaseSpec, Example}

trait BasicSpec extends BaseSpec {
  def parseExample(example: Example): XmlDocument
  def parse(input: String): XmlDocument
  def parseEither(input: String): Either[ParsingException, XmlDocument]
  def print(doc: XmlDocument)(implicit cfg: PrinterConfig): String
}
