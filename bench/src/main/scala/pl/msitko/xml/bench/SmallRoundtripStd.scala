package pl.msitko.xml.bench

import java.io.StringWriter

import scala.xml.XML

object SmallRoundtripStd extends SmallRoundtrip {
  override def roundtrip(input: String): String = {
    val xml = XML.loadString(input)

    val writer = new StringWriter
    XML.write(writer, xml, "UTF-8", true, null)
    writer.toString
  }
}
