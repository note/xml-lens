package net.michalsitko.xml.bench

import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter

object RoundtripLens extends Roundtrip {
  override def roundtrip(input: String): String = {
    val parsed = XmlParser.parse(input).right.get
    XmlPrinter.print(parsed)
  }
}
