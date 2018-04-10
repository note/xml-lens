package pl.msitko.xml.printing

import pl.msitko.xml.test.utils.{Example, ExampleBuilderHelper, ExampleInputs, XmlGenerator}
import pl.msitko.xml.BasicSpec
import pl.msitko.xml.entities._

trait XmlPrinterSpec extends BasicSpec with ExampleInputs with XmlGenerator with ExampleBuilderHelper {
  implicit val printerConfig = PrinterConfig.Default

  def prettyCfg(singleIndent: String) = PrinterConfig(Indent.IndentWith(singleIndent), true)

  "XmlPrinter" should {
    "work for basic example" in {
      check(noNamespaceExample)
    }

    "print XML without any namespace and some whitespaces" in {
      check(noNamespaceXmlStringWithWsExample)
    }

    "print XML with some namespace declarations" in {
      check(namespaceXmlStringExample)
    }

    "print XML with some attributes" in {
      check(attributesXmlStringExample)
    }

    "print XML with attributes with namespaces" in {
      check(attributesWithNsXmlStringExample)
    }

    "deal with very deep XML" in {
      val deepXml = elementOfDepth(4000)
      val doc = XmlDocument.noProlog(deepXml)

      print(doc)
    }

    "not repair undeclared namespace usage" in {
      val xml = labeledElement("b",
        LabeledElement(ResolvedName("undeclaredPrefix", "http://abc.com", "a"), Element(Seq.empty, Seq.empty, Seq.empty))
      )
      val doc = XmlDocument.noProlog(xml)

      // the result is not a valid XML (it's not XmlPrinter responsibility to verify output correctness)
      print(doc) should ===("<b><undeclaredPrefix:a></undeclaredPrefix:a></b>")
    }

    "escape special characters in XML" in {
      val xml = LabeledElement(ResolvedName("", "", "b"), Element(List(Attribute.unprefixed("attr", """a"b'c<d>e&f""")), List(Text("""a"b'c<d>e&f""")), Seq.empty))
      val doc = XmlDocument.noProlog(xml)

      // as you see `"` and `'` are not escaped as they do not have to be escaped in text node
      print(doc) should ===("""<b attr="a&quot;b'c&lt;d&gt;e&amp;f">a"b'c&lt;d&gt;e&amp;f</b>""")
    }

    "escape special characters in namespace declaration value" in {
      val xml = LabeledElement(ResolvedName("", "", "b"), Element(Seq.empty, List(Text("""hello""")), List(NamespaceDeclaration("some", "http://abc.com?a>3&b<7&a=\"name\"&b='cdf'"))))
      val doc = XmlDocument.noProlog(xml)

      print(doc) should ===("""<b xmlns:some="http://abc.com?a&gt;3&amp;b&lt;7&amp;a=&quot;name&quot;&amp;b='cdf'">hello</b>""")
    }

    "not verify output correctness" in {
      val attrs = List(Attribute.unprefixed("attr", "value"), Attribute.unprefixed("attr", "value"), Attribute.unprefixed("attr", "value3"))
      val xml = LabeledElement(ResolvedName("", "", "b"), Element(attrs, List(Text("something")), Seq.empty))
      val doc = XmlDocument.noProlog(xml)

      // the result is not a valid XML (it's not XmlPrinter responsibility to verify output correctness)
      print(doc) should ===("""<b attr="value" attr="value" attr="value3">something</b>""")
    }
  }

  def check(specificExample: Example): Unit = {
    val res = print(specificExample.document)

    // TODO: we don't guarantee preserving whitespace outside of root element
    // decide if it's a good decision
    res.trim should ===(specificExample.stringRepr.trim)
  }

}
