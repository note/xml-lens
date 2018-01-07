package net.michalsitko.xml.bench

object RoundtripLens extends Roundtrip {
  override def roundtrip(input: String): String = {
    val parsed = XmlParser.parse(input).right.get
    XmlPrinter.print(parsed)
  }
}
