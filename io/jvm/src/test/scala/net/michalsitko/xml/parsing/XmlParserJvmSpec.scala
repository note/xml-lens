package net.michalsitko.xml.parsing

import net.michalsitko.xml.BasicJvmSpec
import net.michalsitko.xml.entities.{EntityReference, Text, XmlDocument}

class XmlParserJvmSpec extends XmlParserSpec with BasicJvmSpec {
  // tests written here specifically (as opposed to `XmlParserSpec`) are ones
  // which documents different behavior between JVM and JS implementation

  "parse complete EntityReference" in {
    val res = parse(xmlWithEntityJsVsJvm)

    val expectedRoot = labeledElement("html",
      labeledElement("body",
        labeledElement("p",
          EntityReference("simple", "replacement"),
          Text(" abc "),
          EntityReference("test-entity", "This <em>is</em> an entity."),
          Text(" def"),
          Text("<")
        )
      )
    )

    res.root should === (expectedRoot)
  }

  "respect ParserConfig.replaceEntityReferences == true" in {
    implicit val cfg = ParserConfig.Default.copy(replaceEntityReferences = true)

    val res = parseEitherWithConfig(xmlWithEntityJsVsJvm).right.get

    val expectedRoot = labeledElement("html",
      labeledElement("body",
        labeledElement("p",
          Text("replacement abc "),
          Text("This "),
          labeledElement("em", Text("is")),
          Text(" an entity. def"),
          Text("<")
        )
      )
    )

    res.root should === (expectedRoot)
  }

  "parse basic XML entities as text for replaceEntityReferences == true" in {
    val input = "<a>&amp;&gt;&lt;&quot;&apos;</a>"

    implicit val cfg = ParserConfig.Default.copy(replaceEntityReferences = true)

    val res = parseEitherWithConfig(input).right.get

    res should === (XmlDocument.noProlog(labeledElement("a",
      Text("&"), Text(">"), Text("<"), Text("\""), Text("'")
    )))
  }

}
