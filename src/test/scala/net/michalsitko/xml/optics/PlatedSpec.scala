package net.michalsitko.xml.optics

import monocle.function.Plated
import net.michalsitko.xml.entities.{LabeledElement, Node, Text}
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.utils.ExampleBuilderHelper
import org.scalatest.{Matchers, WordSpec}

class PlatedSpec extends WordSpec with Matchers with ExampleBuilderHelper {
  import OpticsInstances._

  "nodePlated" should {
    "be able to rewrite" in {
      val xml = XmlParser.parse(input).right.get

      val res = Plated.transform[Node] {
        case Text(txt) => Text(txt.toUpperCase)
        case node => node
      }(xml)

      // TODO: get rid of instanceOf
      XmlPrinter.print(res.asInstanceOf[LabeledElement]) should equal (output)
    }

  }

  val input =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f>item1</f>
      |      <h>another item</h>
      |   </c1>
      |</a>""".stripMargin

  val output =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>ITEM1</f>
      |      <g>ITEM2</g>
      |   </c1>
      |   <c1>
      |      <f>ITEM1</f>
      |      <h>ANOTHER ITEM</h>
      |   </c1>
      |</a>""".stripMargin
}
