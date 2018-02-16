package net.michalsitko.xml

import net.michalsitko.xml.entities.XmlDocument
import net.michalsitko.xml.parsing.{ParserConfig, ParsingException, XmlParser}
import net.michalsitko.xml.printing.{PrinterConfig, XmlPrinter}

trait BasicJvmSpec extends BasicSpec {
  def parseEitherWithConfig(input: String)(implicit cfg: ParserConfig): Either[ParsingException, XmlDocument] =
    XmlParser.parse(input)

  def parseEither(input: String): Either[ParsingException, XmlDocument] = XmlParser.parse(input)
  def print(doc: XmlDocument)(implicit cfg: PrinterConfig): String = XmlPrinter.print(doc)
}
