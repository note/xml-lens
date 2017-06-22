package net.michalsitko.xml.bench

import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.syntax.OpticsBuilder.root

object SimpleTransformationLens extends SimpleTransformation {
  override def transform(input: String): String = {
    val parsed = XmlParser.parse(input).right.get

    val traversal = (root \ "f").hasTextOnly
    val res = traversal.modify(_.toUpperCase).apply(parsed)

    XmlPrinter.print(res)
  }
}
