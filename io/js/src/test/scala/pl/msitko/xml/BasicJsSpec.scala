package pl.msitko.xml

import pl.msitko.xml.entities.XmlDocument
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.{PrinterConfig, XmlPrinter}

trait BasicJsSpec extends BasicSpec {
  override def parseEither(input: String) = XmlParser.parse(input)
  override def print(doc: XmlDocument)(implicit cfg: PrinterConfig) = XmlPrinter.print(doc)
}
