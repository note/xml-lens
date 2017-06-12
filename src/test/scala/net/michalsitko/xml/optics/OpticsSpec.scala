package net.michalsitko.xml.optics

import monocle.Traversal
import net.michalsitko.xml.entities._
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.utils.ExampleInputs
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

    "modify existing attribute value" in {
      val parsed = XmlParser.parse(input3).right.get

      val traversal = deep("c1").composeTraversal(deeper("f")).composeOptional(Optics.attribute("someKey"))

      val res = traversal.set("newValue")(parsed)
      XmlPrinter.print(res) should equal(expectedRes3)
    }

    "add attribute" in {
      val parsed = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get

      val traversal = deep("c1").composeTraversal(deeper("f")).composeLens(Optics.attributes)

      val res = traversal.modify(attrs => attrs :+ Attribute.unprefixed("someKey", "newValue"))(parsed)
      XmlPrinter.print(res) should equal(expectedRes4)
    }

    // TODO: think about extracting operation implemented here to library itself
    "modifyExistingOrAdd" in {
      def replaceExistingAttrOrAdd(traversal: Traversal[LabeledElement, Element])(key: ResolvedName, newValue: String): (LabeledElement) => LabeledElement = {
        val replaceIfExists = traversal.composeOptional((Optics.attribute(key)))
        val f1 = replaceIfExists.modify(_ => newValue)
        val addOtherwise = traversal.composeLens(Optics.attributes)
        val f2 = addOtherwise.modify { attrs =>
          if(attrs.exists(_.key == key)) {
            attrs
          } else {
            attrs :+ Attribute(key, newValue)
          }
        }
        f1 andThen f2
      }

      val parsed = XmlParser.parse(input4).right.get

      val traversal = deep("c1").composeTraversal(deeper("f"))

      val res = replaceExistingAttrOrAdd(traversal)(ResolvedName.unprefixed("someKey"), "newValue")(parsed)
      XmlPrinter.print(res) should equal(expectedRes5)
    }

    "delete all attributes" in {
      val parsed = XmlParser.parse(input5).right.get

      val traversal = deep("c1").composeTraversal(deeper("f")).composeLens(Optics.attributes)

      val res = traversal.modify(_ => List.empty)(parsed)
      XmlPrinter.print(res) should equal(expectedRes6)
    }

    "delete single attribute" in {
      val parsed = XmlParser.parse(input5).right.get

      val traversal = deep("c1").composeTraversal(deeper("f")).composeLens(Optics.attributes)

      val res = traversal.modify(attrs => attrs.filter(_.key != ResolvedName.unprefixed("someKey")))(parsed)
      XmlPrinter.print(res) should equal(expectedRes7)
    }

    "delete children" in {
      val parsed = XmlParser.parse(input4).right.get

      val traversal = deep("c1")

      val res = traversal.modify(el => el.copy(children = List.empty))(parsed)
      XmlPrinter.print(res) should equal(expectedRes8)
    }

    "delete specific child" in {
      val parsed = XmlParser.parse(input4).right.get

      val traversal = deep("c1")

      // TODO: it's not terrible, but let's try to think of better way to do it...
      val removeF: Element => Element = { element =>
        val newChildren = element.children.filterNot {
          case el: LabeledElement if el.label == ResolvedName.unprefixed("f") =>
            true
          case _ =>
            false
        }

        element.copy(children = newChildren)
      }

      val res = traversal.modify(removeF)(parsed)
      XmlPrinter.print(res) should equal(expectedRes9)
    }

    "rename element label" in {
      val parsed = XmlParser.parse(input9).right.get

      val renameLabel: Element => Element = { element =>
        val newChildren = element.children.collect {
          case el: LabeledElement if el.label == ResolvedName.unprefixed("f") =>
            el.copy(label = ResolvedName.unprefixed("xyz"))
          case anythingElse =>
            anythingElse
        }
        element.copy(children = newChildren)
      }

      val traversal = deep("c1")

      val res = traversal.modify(renameLabel)(parsed)
      XmlPrinter.print(res) should equal(expectedRes10)
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

  val expectedRes5 =
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

  val expectedRes6 =
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

  val expectedRes7 =
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

  val expectedRes8 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1></c1>
      |   <c1></c1>
      |</a>""".stripMargin

  val expectedRes9 =
    // it's not very readable - we want to have exactly the same whitespaces as before removal
    // TODO: rewrite all those tests so they don't rely on whitespaces treatment
    StringContext.treatEscapes("""<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      \n      <g>item2</g>
      |   </c1>
      |   <c1>
      |      \n      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin)

  val input9 =
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
      |   <c2>
      |     <f>item1</f>
      |   </c2>
      |</a>""".stripMargin

  val expectedRes10 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <xyz>item1</xyz>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <xyz>item1</xyz>
      |      <h>item2</h>
      |   </c1>
      |   <c2>
      |     <f>item1</f>
      |   </c2>
      |</a>""".stripMargin

}
