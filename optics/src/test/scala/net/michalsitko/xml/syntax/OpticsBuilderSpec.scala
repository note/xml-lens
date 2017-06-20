package net.michalsitko.xml.syntax

import net.michalsitko.xml.entities.Attribute
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.syntax.OpticsBuilder._
import net.michalsitko.xml.test.utils.ExampleInputs
import org.scalatest.{Matchers, WordSpec}

class OpticsBuilderSpec extends WordSpec with Matchers with ExampleInputs {
  "OpticsBuilder" should {
    "work" in {
      val parsed = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get

      val traversal = (root \ "c1" \ "f").hasTextOnly
      val res = traversal.set("new").apply(parsed)

      XmlPrinter.print(res) should equal(expectedRes)
    }

    "modify text" in {
      val parsed = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get

      val traversal = (root \ "c1" \ "f").hasTextOnly
      val res = traversal.modify(_.toUpperCase)(parsed)

      XmlPrinter.print(res) should equal(expectedRes2)
    }

    "modify existing attribute value" in {
      val parsed = XmlParser.parse(input3).right.get

      val traversal = (root \ "c1" \ "f").attr("someKey")
      val res = traversal.set("newValue")(parsed)

      XmlPrinter.print(res) should equal(expectedRes3)
    }

    "add attribute" in {
      val parsed = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get

      val traversal = (root \ "c1" \ "f").attrs

      val res = traversal.modify(attrs => attrs :+ Attribute.unprefixed("someKey", "newValue"))(parsed)
      XmlPrinter.print(res) should equal(expectedRes4)
    }
  }

  // TODO: get rid of code duplication
  val expectedRes =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>new</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f>new</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin

  val expectedRes2 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>ITEM1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f>ITEM1</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin

  val input3 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f someKey="oldValue">item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f someKey="oldValue" anotherKey="someValue">item1</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin

  val expectedRes3 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f someKey="newValue">item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f someKey="newValue" anotherKey="someValue">item1</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin

  val expectedRes4 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f someKey="newValue">item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f someKey="newValue">item1</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin


}
