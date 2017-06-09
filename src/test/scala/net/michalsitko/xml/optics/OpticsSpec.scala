package net.michalsitko.xml.optics

import monocle.PTraversal
import net.michalsitko.utils.ExampleInputs
import net.michalsitko.xml.entities.{LabeledElement, ResolvedName, Text}
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter
import org.scalatest.{Matchers, WordSpec}

class OpticsSpec extends WordSpec with Matchers with ExampleInputs {
  "deeper" should {
    "enable to set new Text" in {
      val parsed = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get

      val traversal = deep("c1").composeTraversal(deeper("f"))

      val res = traversal.modify(d => d.copy(children = List(Text("new")))).apply(parsed)
      XmlPrinter.print(res) should equal(expectedRes)
    }

    "modify text" in {
      val parsed = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get

      val traversal = deep("c1").composeTraversal(deeper("f")).composeOptional(Optics.hasTextOnly)

      val res = traversal.modify(_.toUpperCase)(parsed)
      XmlPrinter.print(res) should equal(expectedRes2)
    }
  }

  def deep(label: String) = Optics.deep(ResolvedName.unprefixed(label))
  def deeper(label: String) = Optics.deeper(ResolvedName.unprefixed(label))

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
}
