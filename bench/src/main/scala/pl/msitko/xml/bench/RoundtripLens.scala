package pl.msitko.xml.bench

import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter

object RoundtripLens extends Roundtrip {
  override def roundtrip(input: String): String = {
    val parsed = XmlParser.parse(input).right.get
    XmlPrinter.print(parsed)
  }
}
