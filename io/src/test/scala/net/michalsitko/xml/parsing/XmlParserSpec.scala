package net.michalsitko.xml.parsing

import net.michalsitko.xml.XmlDeclaration
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.test.utils.{Example, ExampleInputs, XmlGenerator}
import org.scalatest.{Matchers, WordSpec}

class XmlParserSpec extends WordSpec with Matchers with ExampleInputs with XmlGenerator {

  "parse" should {
    def checkCorrectInput(specificExample: Example): Unit = {
      val res = XmlParser.parse(specificExample.stringRepr)
      res.right.get should equal(specificExample.nodes)
    }

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

//    "parse DTD" in {
//      checkCorrectInput(xmlWithDtd)
//    }

    "fail for malformed inputs" in {
      malformedXmlStrings.foreach { example =>
        XmlParser.parse(example).isLeft should equal(true)
      }
    }

    "deal with very deep XML" in {
      val input = XmlPrinter.print(List(elementOfDepth(4000)))

      XmlParser.parse(input).isRight should equal(true)
    }

    "deal with empty XMLNS value" in {
      checkCorrectInput(emptyStringAsXmlnsValue)
    }
  }

  "parseWithDeclaration" should {
    def checkCorrectInput(specificExample: Example): Unit = {
      val res = XmlParser.parseWithDeclaration(specificExample.stringRepr)
      res.right.get._2 should equal(specificExample.nodes)
    }

    "pass the same tests as parse does" in {
      checkCorrectInput(noNamespaceExample)
      checkCorrectInput(noNamespaceXmlStringWithWsExample)
      checkCorrectInput(namespaceXmlStringExample)
      checkCorrectInput(attributesXmlStringExample)
      checkCorrectInput(attributesWithNsXmlStringExample)
    }

    "parse XML Declaration" in {
      def test(encoding: String) = {
        val xml =
          s"""<?xml version="1.0" encoding="$encoding"?>
            |<a></a>
          """.stripMargin

        val res = XmlParser.parseWithDeclaration(xml).right.get
        res should equal(
          (Some(XmlDeclaration("1.0", Some(encoding))),
            List(labeledElement("a")))
        )
      }

      test("UTF-8")
      test("ISO-8859-1")
    }

    "parse XML Declaration without encoding" in {
      val xml =
        """<?xml version="1.0"?>
          |<a></a>
        """.stripMargin

      val res = XmlParser.parseWithDeclaration(xml).right.get
      res should equal(
        (Some(XmlDeclaration("1.0", None)),
          List(labeledElement("a")))
      )
    }

    "fail to parse for XML Declaration with empty encoding" in {
      val xml =
        """<?xml version="1.0" encoding=""?>
          |<a></a>
        """.stripMargin

      XmlParser.parseWithDeclaration(xml).isLeft should equal(true)
    }

    "parse XML without Declaration" in {
      val xml =
        """<a></a>
        """.stripMargin

      val res = XmlParser.parseWithDeclaration(xml).right.get
      res should equal((None, List(labeledElement("a"))))
    }

    "fail to parse XML with Declaration with no XML version specified" in {
      {
        val xml =
          """<?xml ?><a></a>
          """.stripMargin

        XmlParser.parseWithDeclaration(xml).isLeft should equal(true)
      }

      {
        val xml =
          """<?xml encoding="UTF-8" ?><a></a>
          """.stripMargin

        XmlParser.parseWithDeclaration(xml).isLeft should equal(true)
      }
    }
  }



}
