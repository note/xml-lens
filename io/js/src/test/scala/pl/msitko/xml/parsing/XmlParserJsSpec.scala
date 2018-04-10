package pl.msitko.xml.parsing

import pl.msitko.xml.BasicJsSpec
import pl.msitko.xml.entities._

class XmlParserJsSpec extends XmlParserSpec with BasicJsSpec {
  // tests written here specifically (as opposed to `XmlParserSpec`) are ones
  // which documents different behavior between JVM and JS implementation

  "parse just entity name (without `replacement`)" in {
    val res = parse(xmlWithEntityJsVsJvm)

    val expectedRoot = labeledElement("html",
      labeledElement("body",
        labeledElement("p",
          EntityReference("simple", ""),
          Text(" abc "),
          EntityReference("test-entity", ""),
          Text(" def"),
          Text("<")
        )
      )
    )

    res.root should === (expectedRoot)
  }
}
