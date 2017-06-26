package net.michalsitko.xml.optics

import monocle.function.Plated
import net.michalsitko.xml.entities.{LabeledElement, Node, ResolvedName, Text}
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.test.utils.ExampleBuilderHelper
import org.scalatest.{Matchers, WordSpec}

class PlatedSpec extends WordSpec with Matchers with ExampleBuilderHelper {
  import OpticsInstances._

  "nodePlated" should {
    "be able to transform all Text nodes" in {
      val xml = XmlParser.parse(input1).right.get

      val res = Plated.transform[Node] {
        case Text(txt) => Text(txt.toUpperCase)
        case node => node
      }(xml)

      // TODO: get rid of instanceOf
      XmlPrinter.print(res.asInstanceOf[LabeledElement]) should equal (output1)
    }

    "be able to transform all nodes with `f` label" in {
      val xml = XmlParser.parse(input2).right.get

      val res = Plated.transform[Node] {
        case el: LabeledElement if el.label == ResolvedName.unprefixed("f") =>
          LabeledElementOptics.children.set(List(Text("something")))(el)
        case node => node
      }(xml)

      // TODO: get rid of instanceOf
      XmlPrinter.print(res.asInstanceOf[LabeledElement]) should equal (output2)
    }

    "be able to transform all `f` labels to `xyz`" in {
      val xml = XmlParser.parse(input2).right.get

      val res = Plated.transform[Node] {
        case el: LabeledElement if el.label == ResolvedName.unprefixed("f") =>
          el.copy(label = ResolvedName.unprefixed("xyz"))
        case node => node
      }(xml)

      XmlPrinter.print(res.asInstanceOf[LabeledElement]) should equal (output3)
    }

    "be able to transform all `f` labels to `xyz` (even at top level)" in {
      val xml = XmlParser.parse(input4).right.get

      val res = Plated.rewrite[Node] {
        case el: LabeledElement if el.label == ResolvedName.unprefixed("f") =>
          Some(el.copy(label = ResolvedName.unprefixed("xyz")))
        case node => None
      }(xml)

      XmlPrinter.print(res.asInstanceOf[LabeledElement]) should equal (output4)
    }

    // TODO: it does not work on top level.
    // Probably Plated works like this but it should be either well documented here or
    // we should provide facade that take care of that
    // See also at implementation of NodeOps.minimize where there's a similar problem
    // TODO: investigate different behavior for rewrite and transform and either document or abstract this difference
    "be able to transform all `f` labels to `xyz` (even at top level) 2" ignore {
      val xml = XmlParser.parse(input4).right.get

      val res = Plated.transform[Node] {
        case el: LabeledElement if el.label == ResolvedName.unprefixed("f") =>
          el.copy(label = ResolvedName.unprefixed("xyz"))
      }(xml)

      XmlPrinter.print(res.asInstanceOf[LabeledElement]) should equal (output4)
    }

    // TODO: there's no possibility to compose Optics with Plated as deep, deeper and so on have `Element` as a target
    // and
    "be able to transform text of all `f` elements in selected, known subtree" ignore {
      val xml = XmlParser.parse(input5).right.get

//      val transform: (Node) => Node = Plated.transform[Node] {
//        case Text(txt) => Text(txt.toUpperCase)
//        case node => node
//      }

      val res = Plated.transform[Node] {
        case el: LabeledElement if el.label == ResolvedName.unprefixed("f") =>
          el.copy(label = ResolvedName.unprefixed("xyz"))
        case node => node
      }(xml)

      XmlPrinter.print(res.asInstanceOf[LabeledElement]) should equal (output5)
    }

  }

  val input1 =
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

  val output1 =
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

  val input2 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a><f>something</f><c1><f>item1</f><g>item2</g></c1><c2><f>item1<f></f></f><h>another item<f></f></h><f><a>item3</a></f></c2></a>""".stripMargin

  val output2 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a><f>something</f><c1><f>something</f><g>item2</g></c1><c2><f>something</f><h>another item<f>something</f></h><f>something</f></c2></a>""".stripMargin

  val output3 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a><xyz>something</xyz><c1><xyz>item1</xyz><g>item2</g></c1><c2><xyz>item1<xyz></xyz></xyz><h>another item<xyz></xyz></h><xyz><a>item3</a></xyz></c2></a>""".stripMargin

  val input4 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<f><f>something</f><c1><f>item1</f><g>item2</g></c1><c2><f>item1<f></f></f><h>another item<f></f></h><f><a>item3</a></f></c2></f>""".stripMargin

  val output4 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<xyz><xyz>something</xyz><c1><xyz>item1</xyz><g>item2</g></c1><c2><xyz>item1<xyz></xyz></xyz><h>another item<xyz></xyz></h><xyz><a>item3</a></xyz></c2></xyz>""".stripMargin

  val input5 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item</f>
      |      <g>item</g>
      |   </c1>
      |   <b>
      |     <f>item</f>
      |     <g>item</g>
      |     <c1>
      |       <f>item</f>
      |       <g>item</g>
      |     </c1>
      |   </b>
      |   <c>
      |     <c1>
      |        <f>item</f>
      |        <h>item</h>
      |     </c1>
      |   </c>
      |</a>""".stripMargin

  val output5 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item</f>
      |      <g>item</g>
      |   </c1>
      |   <b>
      |     <f>ITEM</f>
      |     <g>item</g>
      |     <c1>
      |       <f>ITEM</f>
      |       <g>item</g>
      |     </c1>
      |   </b>
      |   <c>
      |     <c1>
      |        <f>item</f>
      |        <h>item</h>
      |     </c1>
      |   </c>
      |</a>""".stripMargin
}
