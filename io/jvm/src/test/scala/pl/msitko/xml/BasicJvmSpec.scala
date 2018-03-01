package pl.msitko.xml

import pl.msitko.xml.entities.XmlDocument
import pl.msitko.xml.parsing.{ParserConfig, ParsingException, XmlParser}
import pl.msitko.xml.printing.{PrinterConfig, XmlPrinter}

trait BasicJvmSpec extends BasicSpec {
  def parseEitherWithConfig(input: String)(implicit cfg: ParserConfig): Either[ParsingException, XmlDocument] =
    XmlParser.parse(input)

  def parseEither(input: String): Either[ParsingException, XmlDocument] = XmlParser.parse(input)
  def print(doc: XmlDocument)(implicit cfg: PrinterConfig): String = XmlPrinter.print(doc)
}
