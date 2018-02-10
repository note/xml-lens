package net.michalsitko.xml

import net.michalsitko.xml.entities.XmlDocument
import net.michalsitko.xml.parsing.{ParsingException, XmlParser}
import net.michalsitko.xml.printing.{PrinterConfig, XmlPrinter}

trait BasicJvmSpec extends BasicSpec {
  def parseEither(input: String): Either[ParsingException, XmlDocument] = XmlParser.parse(input)
  def print(doc: XmlDocument)(implicit cfg: PrinterConfig): String = XmlPrinter.print(doc)
}
