package net.michalsitko.xml.roundtrips

import net.michalsitko.xml.BasicJvmSpec

class ParserPrinterJvmSpec extends ParserPrinterSpec with BasicJvmSpec {
  // tests written here specifically (as opposed to `ParserPrinterSpec`) are ones
  // which documents different behavior between JVM and JS implementation

  // this behavior is dictated by XMLStreamReader API - I see no way of having different behavior with that API
  "do not preserve entities in attr values" in {
    val parsed = parse(xmlWithEntityInAttrValueInput)
    val printed = print(parsed)
    printed should === (xmlWithEntityInAttrValueOutputJvm)
  }

  val xmlWithEntityInAttrValueOutputJvm =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "EntityVal">
      |]><html xmlns="http://www.w3.org/1999/xhtml">
      |<body>
      |    <h1 attr="someEntityValthing">Entities in XML</h1>
      |</body>
      |</html>""".stripMargin
}
