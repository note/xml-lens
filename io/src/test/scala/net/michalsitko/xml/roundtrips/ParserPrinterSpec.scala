package net.michalsitko.xml.roundtrips

import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.{PrinterConfig, XmlPrinter}
import org.scalatest.{Matchers, WordSpec}

class ParserPrinterSpec extends WordSpec with Matchers {
  "XmlParser and XmlPrinter" should {
    "preserve comments" in {
      val examples = List(exampleXmlString, exampleXmlString2)

      examples.foreach { example =>
        val parsed = XmlParser.parse(example)
        val parsedXml = parsed.right.get
        val printed = XmlPrinter.print(parsedXml)

        printed should equal(example)
      }
    }

    "pretty print" in {
      val xml = XmlParser.parse(uglyXmlString).right.get
      val printed = XmlPrinter.prettyPrint(xml, PrinterConfig(Some("  ")))
      printed should equal(prettyXmlString)
    }

    "pretty print with comments" in {
      val xml = XmlParser.parse(uglyXmlString2).right.get
      val printed = XmlPrinter.prettyPrint(xml, PrinterConfig(Some("  ")))
      printed should equal(prettyXmlString2)
    }

    "PrinterConfig is taken into account" in {
      val xml = XmlParser.parse(uglyXmlString).right.get
      val printed = XmlPrinter.prettyPrint(xml, PrinterConfig(Some(" ")))
      printed should equal(prettyXmlStringIntendedWithOneSpace)
    }
  }

  val exampleXmlString =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<detail>
      |    <band height="20">
      |    <!--
      |      Hello,
      |         I am a multi-line XML comment
      |         <staticText>
      |            <reportElement x="180" y="0" width="200" height="20"/>
      |            <text><![CDATA[Hello World!]]></text>
      |          </staticText>
      |      -->
      |     </band>
      |</detail>""".stripMargin

  val exampleXmlString2 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<detail><band height="20"><!-- hello --></band></detail>""".stripMargin

  val uglyXmlString =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a><c1><f>item  </f>
      |<g>
      |          item</g>
      |   </c1>
      |   <c1>
      |      <f>  </f>
      |      <h>
      |
      |</h>
      |   </c1>
      |   <c1>
      |      <f>item</f>
      |   </c1>
      |<c1>
      |      item
      |      <f>item </f>
      | </c1>
      |</a>""".stripMargin

  val prettyXmlString =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |  <c1>
      |    <f>item  </f>
      |    <g>
      |          item</g>
      |  </c1>
      |  <c1>
      |    <f></f>
      |    <h></h>
      |  </c1>
      |  <c1>
      |    <f>item</f>
      |  </c1>
      |  <c1>
      |      item
      |      
      |    <f>item </f>
      |  </c1>
      |</a>""".stripMargin

  val prettyXmlStringIntendedWithOneSpace =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      | <c1>
      |  <f>item  </f>
      |  <g>
      |          item</g>
      | </c1>
      | <c1>
      |  <f></f>
      |  <h></h>
      | </c1>
      | <c1>
      |  <f>item</f>
      | </c1>
      | <c1>
      |      item
      |      
      |  <f>item </f>
      | </c1>
      |</a>""".stripMargin


  val uglyXmlString2 =
    """<?xml version="1.0" encoding="UTF-8"?>
    |<a><c1 xmlns="http://default.com" xmlns:a="http://a.com"><f someKey="value">item  </f>
    |      <a:g>
    |          item</a:g>
    |   </c1>
    |   <!-- hello here
    |-->
    |</a>""".stripMargin

  val prettyXmlString2 = """<?xml version="1.0" encoding="UTF-8"?>
    |<a>
    |  <c1 xmlns="http://default.com" xmlns:a="http://a.com">
    |    <f someKey="value">item  </f>
    |    <a:g>
    |          item</a:g>
    |  </c1><!-- hello here
    |-->
    |</a>""".stripMargin

}
