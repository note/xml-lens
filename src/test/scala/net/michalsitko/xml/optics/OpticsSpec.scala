package net.michalsitko.xml.optics

import monocle.{PTraversal, Traversal}
import net.michalsitko.utils.ExampleInputs
import net.michalsitko.xml.entities._
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter
import org.scalatest.{Matchers, WordSpec}

class OpticsSpec extends WordSpec with Matchers with ExampleInputs {
  "deeper" should {
//    "enable to set new Text" in {
//      val parsed = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get
//
//      val traversal = deep("c1").composeTraversal(deeper("f"))
//
//      val res = traversal.modify(d => d.copy(children = List(Text("new")))).apply(parsed)
//      XmlPrinter.print(res) should equal(expectedRes)
//    }
//
//    "modify text" in {
//      val parsed = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get
//
//      val traversal = deep("c1").composeTraversal(deeper("f")).composeOptional(Optics.hasTextOnly)
//
//      val res = traversal.modify(_.toUpperCase)(parsed)
//      XmlPrinter.print(res) should equal(expectedRes2)
//    }
//
//    "modify existing attribute value" in {
//      val parsed = XmlParser.parse(input3).right.get
//
//      val traversal = deep("c1").composeTraversal(deeper("f")).composeOptional(Optics.attribute("someKey"))
//
//      val res = traversal.set("newValue")(parsed)
//      XmlPrinter.print(res) should equal(expectedRes3)
//    }
//
//    "add attribute" in {
//      val parsed = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get
//
//      val traversal = deep("c1").composeTraversal(deeper("f")).composeLens(Optics.attributes)
//
//      val res = traversal.modify(attrs => attrs :+ Attribute.unprefixed("someKey", "newValue"))(parsed)
//      val expectedRes = expectedRes3 // we expect the same result as in test above
//      XmlPrinter.print(res) should equal(expectedRes)
//    }
//
//    // TODO: think about extracting operation implemented here to library itself
//    "modifyExistingOrAdd" in {
//      def replaceExistingAttrOrAdd(traversal: Traversal[LabeledElement, Element])(key: ResolvedName, newValue: String): (LabeledElement) => LabeledElement = {
//        val replaceIfExists = traversal.composeOptional((Optics.attribute(key)))
//        val f1 = replaceIfExists.modify(_ => newValue)
//        val addOtherwise = traversal.composeLens(Optics.attributes)
//        val f2 = addOtherwise.modify { attrs =>
//          if(attrs.exists(_.key == key)) {
//            attrs
//          } else {
//            attrs :+ Attribute(key, newValue)
//          }
//        }
//        f1 andThen f2
//      }
//
//      val parsed = XmlParser.parse(input4).right.get
//
//      val traversal = deep("c1").composeTraversal(deeper("f"))
//
//      val res = replaceExistingAttrOrAdd(traversal)(ResolvedName.unprefixed("someKey"), "newValue")(parsed)
//      XmlPrinter.print(res) should equal(expectedRes4)
//    }
//
//    "delete all attributes" in {
//      val parsed = XmlParser.parse(input5).right.get
//
//      val traversal = deep("c1").composeTraversal(deeper("f")).composeLens(Optics.attributes)
//
//      val res = traversal.modify(_ => List.empty)(parsed)
//      XmlPrinter.print(res) should equal(expectedRes5)
//    }
//
//    "delete single attribute" in {
//      val parsed = XmlParser.parse(input5).right.get
//
//      val traversal = deep("c1").composeTraversal(deeper("f")).composeLens(Optics.attributes)
//
//      val res = traversal.modify(attrs => attrs.filter(_.key != ResolvedName.unprefixed("someKey")))(parsed)
//      XmlPrinter.print(res) should equal(expectedRes6)
//    }

    "delete me" in {
      val parsed = XmlParser.parse(input5).right.get

      val tmp = Optics.hasTextOnly.set("")(parsed.element)
      val tmp2 = Optics.hasTextOnly.getOption(tmp)
      println("tmp2: " + tmp2)

//      val input = Element(List(),List(LabeledElement(ResolvedName(,None,a),Element(List(),List(Text("Hello XML")),List()))),List())
//      Optics.hasTextOnly.set("fdas")(input)
//      Optics.hasTextOnly.set("fdas")(input)

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

  val input3 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f someKey="oldValue">item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f someKey="oldValue">item1</f>
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
      |      <f someKey="newValue">item1</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin

  val input4 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f someKey="oldValue">item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f>item1</f>
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

  val input5 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f someKey="oldValue" anotherKey="value">item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f someKey="oldValue">item1</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin

  val expectedRes5 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f>item1</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin

  val expectedRes6 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f anotherKey="value">item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f>item1</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin
}
