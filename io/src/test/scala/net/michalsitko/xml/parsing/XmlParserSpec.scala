package net.michalsitko.xml.parsing

import net.michalsitko.xml.BasicSpec
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.test.utils.{Example, ExampleInputs, XmlGenerator}
import net.michalsitko.xml.utils.XmlDocumentFactory

class XmlParserSpec extends BasicSpec with ExampleInputs with XmlGenerator {
  implicit val parserConfig = XmlParser.DefaultParserConfig
  implicit val printerConfig = XmlPrinter.DefaultPrinterConfig

  "parse" should {
    def checkCorrectInput(specificExample: Example): Unit =
      parseExample(specificExample) should === (specificExample.document)

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

    "parse DTD" in {
      checkCorrectInput(xmlWithDtd)
    }

    "parse Processing Instructions" in {
      checkCorrectInput(xmlWithPI)
    }

    "parse CData" in {
      checkCorrectInput(xmlWithCData)
    }

    "fail for malformed inputs" in {
      malformedXmlStrings.foreach { example =>
        XmlParser.parse(example).isLeft should === (true)
      }
    }

    "deal with very deep XML" in {
      val input = XmlPrinter.print(XmlDocumentFactory.noProlog(elementOfDepth(4000)))

      XmlParser.parse(input).isRight should === (true)
    }

    "deal with empty XMLNS value" in {
      checkCorrectInput(emptyStringAsXmlnsValue)
    }
  }

  "parseWithDeclaration" should {
    def checkCorrectInput(specificExample: Example): Unit = {
      parseExample(specificExample) should === (specificExample.document)
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

        parse(xml) should === (XmlDocumentFactory.withProlog("1.0", Some(encoding), labeledElement("a")))
      }

      test("UTF-8")
      test("ISO-8859-1")
    }

    "parse XML Declaration without encoding" in {
      val xml =
        """<?xml version="1.0"?>
          |<a></a>
        """.stripMargin

      parse(xml) should === (XmlDocumentFactory.withProlog("1.0", None, labeledElement("a")))
    }

    "fail to parse for XML Declaration with empty encoding" in {
      val xml =
        """<?xml version="1.0" encoding=""?>
          |<a></a>
        """.stripMargin

      XmlParser.parse(xml).isLeft should === (true)
    }

    "parse XML without Declaration" in {
      val xml =
        """<a></a>
        """.stripMargin

      parse(xml) should === (XmlDocumentFactory.noProlog(labeledElement("a")))
    }

    "fail to parse XML with Declaration with no XML version specified" in {
      {
        val xml =
          """<?xml ?><a></a>
          """.stripMargin

        XmlParser.parse(xml).isLeft should === (true)
      }

      {
        val xml =
          """<?xml encoding="UTF-8" ?><a></a>
          """.stripMargin

        XmlParser.parse(xml).isLeft should === (true)
      }
    }
  }

}
