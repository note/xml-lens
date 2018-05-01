package pl.msitko.xml.bench

import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter
import pl.msitko.xml.dsl.OpticsBuilder.root

object SimpleTransformationLens extends SimpleTransformation {
  override def transform(input: String): String = {
    val parsed = XmlParser.parse(input).right.get

    val modify = (root \ "interesting" \ "special").hasTextOnly.modify(_.toUpperCase)
    val res = modify(parsed)

    XmlPrinter.print(res)
  }
}
