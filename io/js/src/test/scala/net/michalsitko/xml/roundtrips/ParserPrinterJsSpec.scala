package net.michalsitko.xml.roundtrips

import net.michalsitko.xml.BasicJsSpec

class ParserPrinterJsSpec extends ParserPrinterSpec with BasicJsSpec {
  // this behavior is dictated by XMLStreamReader API - I see no way of having different behavior with that API
  "do not preserve entities in attr values" in {
    val parsed = parse(xmlWithEntityInAttrValueInput)
    val printed = print(parsed)
    printed should === (xmlWithEntityInAttrValueOutput)
  }
}
