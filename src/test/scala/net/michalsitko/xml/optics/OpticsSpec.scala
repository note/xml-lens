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

      println("res: " + XmlPrinter.print(res))
    }
  }

  def deeper(label: String) = Optics.deeper(ResolvedName.unprefixed(label))
  def deeper2(label: String) = Optics.deeperDetails(ResolvedName.unprefixed(label))


}
