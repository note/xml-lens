package net.michalsitko.xml.optics

import net.michalsitko.utils.ExampleInputs
import net.michalsitko.xml.entities.{ResolvedName, Text}
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter
import org.scalatest.{Matchers, WordSpec}

class OpticsSpec extends WordSpec with Matchers with ExampleInputs {
  "deeper" should {
    "work" in {
      val parsed = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get

      val traversal = deeper("c1").composeTraversal(deeper2("f")).modify(d => d.copy(children = List(Text("new"))))

      val res = traversal.apply(parsed)
      XmlPrinter.print(res) should equal(expectedRes)
    }
  }

  def deeper(label: String) = Optics.deep(ResolvedName.unprefixed(label))
  def deeper2(label: String) = Optics.deeper(ResolvedName.unprefixed(label))

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
}
