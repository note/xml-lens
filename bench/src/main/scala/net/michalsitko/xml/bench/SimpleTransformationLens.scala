package net.michalsitko.xml.bench

import net.michalsitko.xml.syntax.OpticsBuilder.root

object SimpleTransformationLens extends SimpleTransformation {
  override def transform(input: String): String = {
    val parsed = XmlParser.parse(input).right.get

    val modify = (root \ "f").hasTextOnly.modify(_.toUpperCase)
    val res = modify(parsed)

    XmlPrinter.print(res)
  }
}
