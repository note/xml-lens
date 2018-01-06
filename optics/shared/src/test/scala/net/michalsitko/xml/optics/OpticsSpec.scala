package net.michalsitko.xml.optics

import monocle.Traversal
import net.michalsitko.xml.BasicSpec
import net.michalsitko.xml.entities._
import net.michalsitko.xml.optics.ElementOptics._
import net.michalsitko.xml.optics.LabeledElementOptics._
import net.michalsitko.xml.optics.XmlDocumentOptics._
import net.michalsitko.xml.printing.PrinterConfig
import net.michalsitko.xml.test.utils.ExampleInputs

trait OpticsSpec extends BasicSpec with ExampleInputs {
  implicit val printerConfig = PrinterConfig.Default

  "deeper" should {
    "enable to set new Text" in {
      val parsed = parseExample(noNamespaceXmlStringWithWsExample)

      val traversal = rootLens.composeTraversal(deep("c1").composeTraversal(deeper("f")).composeOptional(hasTextOnly))

      val res = traversal.set("new").apply(parsed)
      print(res) should ===(expectedRes)
    }

    "modify text" in {
      val parsed = parseExample(noNamespaceXmlStringWithWsExample)

      val traversal = rootLens.composeTraversal(deep("c1").composeTraversal(deeper("f")).composeOptional(hasTextOnly))

      val res = traversal.modify(_.toUpperCase)(parsed)
      print(res) should ===(expectedRes2)
    }

    "modify existing attribute value" in {
      val parsed = parse(input3)

      val traversal = rootLens.composeTraversal(deep("c1").composeTraversal(deeper("f")).composeOptional(attribute("someKey")))

      val res = traversal.set("newValue")(parsed)
      print(res) should ===(expectedRes3)
    }

    "add attribute" in {
      val parsed = parseExample(noNamespaceXmlStringWithWsExample)

      val traversal = rootLens.composeTraversal(deep("c1").composeTraversal(deeper("f")).composeLens(attributes))

      val res = traversal.modify(attrs => attrs :+ Attribute.unprefixed("someKey", "newValue"))(parsed)
      print(res) should ===(expectedRes4)
    }

    "modifyExistingOrAdd" in {
      def replaceExistingAttrOrAdd(traversal: Traversal[XmlDocument, Element])(key: String, newValue: String): (XmlDocument) => XmlDocument = {
        val keyMatcher = NameMatcher.fromString(key)
        val replaceIfExists = traversal.composeOptional((attribute(keyMatcher)))
        val f1 = replaceIfExists.modify(_ => newValue)
        val addOtherwise = traversal.composeLens(attributes)
        val f2 = addOtherwise.modify { attrs =>
          if(attrs.exists(attr => keyMatcher.matches(attr.key))) {
            attrs
          } else {
            attrs :+ Attribute(ResolvedName.unprefixed(key), newValue)
          }
        }
        f1 andThen f2
      }

      val parsed = parse(input4)

      val traversal = rootLens.composeTraversal(deep("c1").composeTraversal(deeper("f")))

      val res = replaceExistingAttrOrAdd(traversal)("someKey", "newValue")(parsed)
      print(res) should ===(expectedRes5)
    }

    "delete all attributes" in {
      val parsed = parse(input5)

      val traversal = rootLens.composeTraversal(deep("c1").composeTraversal(deeper("f")).composeLens(attributes))

      val res = traversal.modify(_ => List.empty)(parsed)
      print(res) should ===(expectedRes6)
    }

    "delete single attribute" in {
      val parsed = parse(input5)

      val traversal = rootLens.composeTraversal(deep("c1").composeTraversal(deeper("f")).composeLens(attributes))

      val res = traversal.modify(attrs => attrs.filter(_.key != ResolvedName.unprefixed("someKey")))(parsed)
      print(res) should ===(expectedRes7)
    }

    "delete children" in {
      val parsed = parse(input4)

      val traversal = rootLens.composeTraversal(deep("c1"))

      val res = traversal.modify(el => el.copy(children = List.empty))(parsed)
      print(res) should ===(expectedRes8)
    }

    "delete specific child" in {
      val parsed = parse(input4)

      val traversal = rootLens.composeTraversal(deep("c1"))

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
      print(res) should ===(expectedRes9)
    }

    "rename element label" in {
      val parsed = parse(input9)
      val nameMatcher = NameMatcher.fromString("f")

      val renameLabel = { element: Element =>
        ElementOptics.allLabeledElements.modify { el =>
          if (nameMatcher.matches(el.label)) {
            localName.set("xyz")(el)
          } else {
            el
          }
        }(element)
      }

      val traversal = rootLens.composeTraversal(deep("c1"))

      val res = traversal.modify(renameLabel)(parsed)
      print(res) should ===(expectedRes10)
    }

    "modify element's child node based on existence of another child" in {
      val parsed = parse(input11)

      val modFun: Element => Element = { el =>
        deeper("h").composeOptional(hasTextOnly).headOption(el).map { textInH =>
          deeper("f").composeLens(ElementOptics.children).set(List(Text(textInH)))(el)
        }.getOrElse(el)
      }

      val res = rootLens.composeTraversal(deep("c1")).modify(modFun)(parsed)
      print(res) should ===(output11)
    }

    "do a few modifications" in {
      val parsed = parse(input12)

      val addAttr = attributes.modify(attrs => attrs :+ Attribute(ResolvedName.unprefixed("someKey"), "someValue"))
      val modifyText = hasTextOnly.modify(_.toUpperCase)

      val traversal = rootLens.composeTraversal(deep("c1").composeTraversal(deeper("f")))
      val res = traversal.modify(addAttr andThen modifyText)(parsed)

      print(res) should ===(output12)
    }

  }

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

  val input11 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item</f>
      |      <h>something</h>
      |   </c1>
      |   <c1>
      |      <f>item</f>
      |      <g>item</g>
      |   </c1>
      |   <c1>
      |      <f>item</f>
      |      <h>something</h>
      |   </c1>
      |</a>""".stripMargin

  val output11 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>something</f>
      |      <h>something</h>
      |   </c1>
      |   <c1>
      |      <f>item</f>
      |      <g>item</g>
      |   </c1>
      |   <c1>
      |      <f>something</f>
      |      <h>something</h>
      |   </c1>
      |</a>""".stripMargin

  val input12 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item</f>
      |      <h>something</h>
      |   </c1>
      |   <c1>
      |      <f>item</f>
      |      <g>item</g>
      |   </c1>
      |</a>""".stripMargin


  val output12 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f someKey="someValue">ITEM</f>
      |      <h>something</h>
      |   </c1>
      |   <c1>
      |      <f someKey="someValue">ITEM</f>
      |      <g>item</g>
      |   </c1>
      |</a>""".stripMargin
}
