package net.michalsitko.xml.printing

import net.michalsitko.xml.BasicSpec
import net.michalsitko.xml.entities._
import net.michalsitko.xml.test.utils._
import net.michalsitko.xml.utils.XmlDocumentFactory

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
      val doc = XmlDocumentFactory.noProlog(deepXml)

      print(doc)
    }

    "not repair undeclared namespace usage" in {
      val xml = labeledElement("b",
        LabeledElement(ResolvedName("undeclaredPrefix", "http://abc.com", "a"), Element(Seq.empty, Seq.empty, Seq.empty))
      )
      val doc = XmlDocumentFactory.noProlog(xml)

      // the result is not a valid XML (it's not XmlPrinter responsibility to verify output correctness)
      print(doc) should ===("<b><undeclaredPrefix:a></undeclaredPrefix:a></b>")
    }

    "escape special characters in XML" in {
      val xml = LabeledElement(ResolvedName("", "", "b"), Element(List(Attribute.unprefixed("attr", """a"b'c<d>e&f""")), List(Text("""a"b'c<d>e&f""")), Seq.empty))
      val doc = XmlDocumentFactory.noProlog(xml)

      // as you see `"` and `'` are not escaped as they do not have to be escaped in text node
      print(doc) should ===("""<b attr="a&quot;b'c&lt;d&gt;e&amp;f">a"b'c&lt;d&gt;e&amp;f</b>""")
    }

    "not verify output correctness" in {
      val attrs = List(Attribute.unprefixed("attr", "value"), Attribute.unprefixed("attr", "value"), Attribute.unprefixed("attr", "value3"))
      val xml = LabeledElement(ResolvedName("", "", "b"), Element(attrs, List(Text("something")), Seq.empty))
      val doc = XmlDocumentFactory.noProlog(xml)

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
