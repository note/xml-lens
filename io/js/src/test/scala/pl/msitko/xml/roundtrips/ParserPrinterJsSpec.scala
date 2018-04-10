package pl.msitko.xml.roundtrips

import pl.msitko.xml.BasicJsSpec

class ParserPrinterJsSpec extends ParserPrinterSpec with BasicJsSpec {
  // tests written here specifically (as opposed to `ParserPrinterSpec`) are ones
  // which documents different behavior between JVM and JS implementation

  "do not preserve entities in attr values" in {
    val parsed = parse(xmlWithEntityInAttrValueInput)
    val printed = print(parsed)
    printed should === (xmlWithEntityInAttrValueOutputJs)
  }

  val xmlWithEntityInAttrValueOutputJs =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "EntityVal">
      |]><html xmlns="http://www.w3.org/1999/xhtml">
      |<body>
      |    <h1 attr="some&amp;test-entity;thing">Entities in XML</h1>
      |</body>
      |</html>""".stripMargin
}
