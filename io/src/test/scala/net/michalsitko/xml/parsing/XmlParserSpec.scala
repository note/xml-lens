package net.michalsitko.xml.parsing

import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.test.utils.{Example, ExampleInputs, XmlGenerator}
import org.scalatest.{Matchers, WordSpec}

class XmlParserSpec extends WordSpec with Matchers with ExampleInputs with XmlGenerator {
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

    "parse comments" in {
      commentsExamples.foreach(checkCorrectInput)
    }

    "fail for malformed inputs" in {
      malformedXmlStrings.foreach { example =>
        XmlParser.parse(example).isLeft should equal(true)
      }
    }

    "deal with very deep XML" in {
      val input = XmlPrinter.print(elementOfDepth(4000))

      XmlParser.parse(input).isRight should equal(true)
    }

    "deal with empty XMLN value" in {
      checkCorrectInput(emptyStringAsXmlnsValue)
    }

  }

  def checkCorrectInput(specificExample: Example): Unit = {
    val res = XmlParser.parse(specificExample.stringRepr)
    res.right.get should equal(specificExample.node)
  }

}
