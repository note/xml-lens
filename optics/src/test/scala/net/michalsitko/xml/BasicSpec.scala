package net.michalsitko.xml

import net.michalsitko.xml.entities.XmlDocument
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.test.utils.{BaseSpec, Example}

trait BasicSpec extends BaseSpec {
  def parseExample(example: Example): XmlDocument = XmlParser.parse(example.stringRepr).right.get
  def parse(input: String): XmlDocument = XmlParser.parse(input).right.get
}
