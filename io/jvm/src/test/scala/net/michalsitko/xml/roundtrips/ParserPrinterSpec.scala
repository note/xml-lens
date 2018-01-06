package net.michalsitko.xml.roundtrips

import net.michalsitko.xml.BasicSpec
import net.michalsitko.xml.printing.{Indent, PrinterConfig, XmlPrinter}
import net.michalsitko.xml.test.utils.ExampleInputs

class ParserPrinterSpec extends BasicSpec with ExampleInputs {
  def testForInputs(inputs: String*): Unit = {
    inputs.foreach { example =>
      val parsed = parse(example)
      val printed = XmlPrinter.print(parsed)

      printed should === (example)
    }
  }

  "XmlParser and XmlPrinter" should {
    "preserve comments" in {
      testForInputs(exampleXmlString, exampleXmlString2)
    }

    "preserve DTD" in {
      testForInputs(exampleXmlWithDtd, exampleXmlWithDtd2)
    }

    "preseve Processing Instructions" in {
      testForInputs(exampleWithPI)
    }

    "preserve CData" in {
      testForInputs(xmlWithCData.stringRepr)
    }

    "preserve empty element" in {
      val examples = List(xmlWithSelfClosingTag, xmlWithEmptyElement)

      examples.foreach { example =>
        val parsed = parse(example)
        val printed = XmlPrinter.print(parsed)

        printed should === (xmlWithEmptyElement)
      }
    }

    "preserve entities" in {
      val parsed = parse(xmlWithEntity)
      val printed = XmlPrinter.print(parsed)
      printed should === (xmlWithEntity)
    }

    "pretty print" in {
      val xml = parse(uglyXmlString)
      val prettyCfg = PrinterConfig(Indent.IndentWith("  "))

      XmlPrinter.print(xml)(prettyCfg)  should === (prettyXmlString)
      XmlPrinter.print(xml)             should === (uglyXmlString)
    }

    "pretty print with comments" in {
      val xml = parse(uglyXmlString2)
      val printed = XmlPrinter.print(xml)(PrinterConfig(Indent.IndentWith("  ")))
      printed should === (prettyXmlString2)
    }

    "PrinterConfig is taken into account" in {
      val xml = parse(uglyXmlString)
      val printed = XmlPrinter.print(xml)(PrinterConfig(Indent.IndentWith(" ")))
      printed should === (prettyXmlStringIntendedWithOneSpace)
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

  val xmlWithSelfClosingTag =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<detail>
      |    <band height="20" />
      |</detail>""".stripMargin

  val xmlWithEmptyElement =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<detail>
      |    <band height="20"></band>
      |</detail>""".stripMargin

    val xmlWithEntity =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "This <em>is</em> an entity.">
      |]><html xmlns="http://www.w3.org/1999/xhtml">
      |<head>
      |    <meta http-equiv="Content-Type" content="application/xhtml+xml;charset=utf-8"></meta>
      |    <title>Entities in XML</title>
      |</head>
      |<body>
      |    <h1>Entities in XML</h1>
      |    <p>&test-entity;</p>
      |    <p>You can use it anywhere you'd use a standard XHTML entity:</p>
      |    <pre>&test-entity;</pre>
      |</body>
      |</html>""".stripMargin

  val exampleXmlString2 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<detail><band height="20"><!-- hello --></band></detail>""".stripMargin

  val exampleXmlWithDtd =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE note SYSTEM "Note.dtd"><note>
      |<to>Tove</to>
      |<from>Jani</from>
      |<heading>Reminder</heading>
      |<body>Don't forget me this weekend!</body>
      |</note>""".stripMargin

  val exampleXmlWithDtd2 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "This <em>is</em> an entity.">
      |]><note></note>""".stripMargin

  // TODO: would fail if we add new lines between top-level Processing instructions
  val exampleWithPI =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<?xml-stylesheet type="text/xsl" href="style.xsl"?><?welcome to pg = 10 of tutorials point?><?welcome ?><note>something<?mso-application progid="Excel.Sheet"?>else</note>""".stripMargin

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
