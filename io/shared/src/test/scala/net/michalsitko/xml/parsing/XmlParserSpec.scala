package net.michalsitko.xml.parsing

import net.michalsitko.xml.BasicSpec
import net.michalsitko.xml.entities.{Text, XmlDocument}
import net.michalsitko.xml.printing.PrinterConfig
import net.michalsitko.xml.test.utils.{Example, ExampleInputs, XmlGenerator}
import net.michalsitko.xml.utils.XmlDocumentFactory

trait XmlParserSpec extends BasicSpec with ExampleInputs with XmlGenerator {
  implicit val printerConfig = PrinterConfig.Default

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

    "parse attributes in case-sensitive fashion" in {
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

    "fail on parsing DTD withing root element" in {
      parseEither(xmlWithDtdIncorrectly).isLeft should === (true)
    }

    "parse Processing Instructions" in {
      checkCorrectInput(xmlWithPI)
    }

    "parse CData" in {
      checkCorrectInput(xmlWithCData)
    }

    "fail for malformed inputs" in {
      malformedXmlStrings.take(6).foreach { example =>
        parseEither(example).isLeft should === (true)
      }
    }

    "deal with very deep XML" in {
      val input = print(XmlDocumentFactory.noProlog(elementOfDepth(4000)))

      parseEither(input).isRight should === (true)
    }

    "deal with empty xmlns value" in {
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

      parseEither(xml).isLeft should === (true)
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

        parseEither(xml).isLeft should === (true)
      }

      {
        val xml =
          """<?xml encoding="UTF-8" ?><a></a>
          """.stripMargin

        parseEither(xml).isLeft should === (true)
      }
    }

    "parse basic XML entities as text" in {
      val xml = "<a>&amp;&gt;&lt;&quot;&apos;</a>"

      parse(xml) should === (XmlDocument.noProlog(labeledElement("a",
        Text("&"), Text(">"), Text("<"), Text("\""), Text("'")
      )))
    }
  }

  val xmlWithEntityJsVsJvm =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "This <em>is</em> an entity.">
      |    <!ENTITY simple "replacement">
      |]><html><body><p>&simple; abc &test-entity; def&lt;</p></body></html>""".stripMargin

}
