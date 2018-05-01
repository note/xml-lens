package pl.msitko.xml.roundtrips

import pl.msitko.xml.printing.{Indent, PrinterConfig}
import pl.msitko.xml.test.utils.ExampleInputs
import pl.msitko.xml.BasicSpec

trait ParserPrinterSpec extends BasicSpec with ExampleInputs {
  implicit val defaultPrinterConfig = PrinterConfig.Default

  def testForInputs(inputs: String*): Unit = {
    inputs.foreach { example =>
      val parsed = parse(example)
      val printed = print(parsed)

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

    "preserve self-closing and empty elements as empty element" in {
      val examples = List(xmlWithSelfClosingTag, xmlWithEmptyElement)

      examples.foreach { example =>
        val parsed = parse(example)
        val printed = print(parsed)

        printed should === (xmlWithEmptyElement)
      }
    }

    "preserve entities in text" in {
      val parsed = parse(xmlWithEntity)
      val printed = print(parsed)
      printed should === (xmlWithEntity)
    }

    // inspired by https://github.com/isaacs/sax-js/issues/35
    // resolved here: https://github.com/isaacs/sax-js/commit/966e19c2e7aa7a605498362f6322038eb87505ec
    // seems like XMLStreamReader does some strange things for numeric entities, getting very unexpected result
    // have not investigated it yet
    "handle numeric entities" ignore {
      val parsed = parse(numericEntity)
      val printed = print(parsed)
      printed should === (numericEntity)
    }

    "pretty print" in {
      val xml = parse(uglyXmlString)
      val prettyCfg = PrinterConfig(Indent.IndentWith("  "), true)

      print(xml)(prettyCfg)  should === (prettyXmlString)
      print(xml)             should === (uglyXmlString)
    }

    "pretty print with comments" in {
      val xml = parse(uglyXmlString2)
      val printed = print(xml)(PrinterConfig(Indent.IndentWith("  "), true))
      printed should === (prettyXmlString2)
    }

    "PrinterConfig is taken into account" in {
      val xml = parse(uglyXmlString)
      val printed = print(xml)(PrinterConfig(Indent.IndentWith(" "), true))
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
      |    <p>abc &test-entity; def</p>
      |    <p>You can use it anywhere you'd use a standard XHTML entity:</p>
      |    <pre>&test-entity;</pre>
      |</body>
      |</html>""".stripMargin

  val xmlWithEntityInAttrValueInput =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "EntityVal">
      |]><html xmlns="http://www.w3.org/1999/xhtml">
      |<body>
      |    <h1 attr="some&test-entity;thing">Entities in XML</h1>
      |</body>
      |</html>""".stripMargin

  val numericEntity =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a><b>&#xd;&#x0d;</b></a>""".stripMargin

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
