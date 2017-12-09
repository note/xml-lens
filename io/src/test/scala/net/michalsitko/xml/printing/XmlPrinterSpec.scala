package net.michalsitko.xml.printing

import net.michalsitko.xml.test.utils.{Example, ExampleInputs, XmlGenerator}
import net.michalsitko.xml.utils.XmlDocumentFactory
import org.scalatest.{Matchers, WordSpec}

class XmlPrinterSpec extends WordSpec with Matchers with ExampleInputs with XmlGenerator {
  implicit val printerConfig = XmlPrinter.DefaultPrinterConfig.copy(identWith = None)

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

      val doc = XmlDocumentFactory.root(deepXml)
      XmlPrinter.print(doc)
    }
  }

  def check(specificExample: Example): Unit = {
    val res = XmlPrinter.print(specificExample.document)

    // TODO: we don't guarantee preserving whitespace outside of root element
    // decide if it's a good decision
    res.trim should equal(specificExample.stringRepr.trim)
  }

}
