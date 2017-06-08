package net.michalsitko.parsing

import net.michalsitko.utils.{Example, ExampleInputs}
import org.scalatest.{Matchers, WordSpec}

class XmlParserSpec extends WordSpec with Matchers with ExampleInputs {
  "XmlParser" should {
    "return proper Element for XML without any namespaces declared and with no whitespaces" in {
      checkCorrectInput(noNamespaceExample)
    }

    "return proper Element for XML without any namespace and some whitespaces" in {
      checkCorrectInput(noNamespaceXmlStringWithWsExample)
    }

    "return proper Element for XML with some namespaces declared" in {
      checkCorrectInput(namespaceXmlStringExample)
    }

    "parse attributes" in {
      checkCorrectInput(attributesXmlStringExample)
    }

    "parse attributes with namespaces" in {
      checkCorrectInput(attributesWithNsXmlStringExample)
    }

    "fail for malformed inputs" in {
      XmlParser.parse(malformedXmlString).isLeft should equal(true)
      XmlParser.parse(malformedXmlString2).isLeft should equal(true)
      XmlParser.parse(malformedNamespaces).isLeft should equal(true)
    }
  }

  def checkCorrectInput(specificExample: Example): Unit = {
    val res = XmlParser.parse(specificExample.stringRepr)
    res should equal(Right(specificExample.tree))
  }

}
