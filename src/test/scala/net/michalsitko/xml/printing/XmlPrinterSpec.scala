package net.michalsitko.xml.printing

import net.michalsitko.utils.{Example, ExampleInputs}
import org.scalatest.{Matchers, WordSpec}

class XmlPrinterSpec extends WordSpec with Matchers with ExampleInputs {
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
  }

  def check(specificExample: Example): Unit = {
    val res = XmlPrinter.print(specificExample.tree)
    println(s"out: $res")

    // TODO: we don't guarantee preserving whitespace outside of root element
    // decide if it's a good decision
    res.trim should equal(specificExample.stringRepr.trim)
  }

}
