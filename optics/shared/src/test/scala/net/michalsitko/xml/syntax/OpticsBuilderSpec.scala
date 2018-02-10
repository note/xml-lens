package net.michalsitko.xml.syntax

import net.michalsitko.xml.BasicSpec
import net.michalsitko.xml.entities.{Attribute, Element, LabeledElement}
import net.michalsitko.xml.optics._
import net.michalsitko.xml.printing.{Indent, PrinterConfig}
import net.michalsitko.xml.syntax.OpticsBuilder._
import net.michalsitko.xml.syntax.document._
import net.michalsitko.xml.test.utils.ExampleInputs

trait OpticsBuilderSpec extends BasicSpec with ExampleInputs {
  implicit val printerConfig = PrinterConfig.Default

  "OpticsBuilder" should {
    "set text for chosen path" in {
      val parsed = parseExample(noNamespaceXmlStringWithWsExample)

      val traversal = (root \ "c1" \ "f").hasTextOnly
      val res = traversal.set("new")(parsed)

      print(res) should === (expectedRes)
    }

    "modify text for chosen path" in {
      val parsed = parseExample(noNamespaceXmlStringWithWsExample)

      val traversal = (root \ "c1" \ "f").hasTextOnly
      val res = traversal.modify(_.toUpperCase)(parsed)

      print(res) should === (expectedRes2)
    }

    "modify existing attribute value" in {
      val parsed = parse(input3)

      val traversal = (root \ "c1" \ "f").attr("someKey")
      val res = traversal.set("newValue")(parsed)

      print(res) should === (expectedRes3)
    }

    "add attribute" in {
      val parsed = parseExample(noNamespaceXmlStringWithWsExample)

      val traversal = (root \ "c1" \ "f").attrs

      val res = traversal.modify(attrs => attrs :+ Attribute.unprefixed("someKey", "newValue"))(parsed)
      print(res) should === (expectedRes4)
    }

    "replaceOrAddAttr" in {
      val parsed = parse(input5)

      val traversal = (root \ "c1" \ "f")

      val res = traversal.replaceOrAddAttr("someKey", "newValue")(parsed)

      print(res) should === (expectedRes4)
    }

    "replaceOrAddAttr for ResolvedNameMatcher" in {
      val parsed = parse(input6)

      val ns = PrefixedNamespace("a", "http://a.com")
      val traversal = (root \ "c1" \ "f")

      val res = traversal.replaceOrAddAttr(ns.name("someKey"), "newValue")(parsed)

      print(res) should === (expectedRes6)
    }

    "modify attribute for ResolvedNameMatcher" in {
      val parsed = parse(input7)

      val ns = Namespace("http://a.com")
      val traversal = (root \ "c1" \ "f").attr(ns.name("someKey"))

      val res = traversal.modify(_.toUpperCase)(parsed)
      print(res) should === (expectedRes7)
    }

    "modify attribute for IgnoreNamespaceMatcher" in {
      val parsed = parse(input7)

      val traversal = (root \ "c1" \ "f").attr("someKey")

      val res = traversal.modify(_.toUpperCase)(parsed)
      print(res) should === (expectedRes8)
    }

    "modify attribute for ResolvedNameMatcher2" in {
      val parsed = parse(input7)

      val ns = Namespace("")
      val traversal = (root \ "c1" \ "f").attr(ns.name("someKey"))

      val res = traversal.modify(_.toUpperCase)(parsed)
      print(res) should === (expectedRes9)
    }

    "modify attribute in root element" in {
      val parsed = parse(input10)

      val traversal = root.attr("someKey")

      val res = traversal.set("newValue")(parsed)
      print(res) should === (expectedRes10)
    }

    "modify text in root element" in {
      val parsed = parse(input10)

      val res = root.hasTextOnly.set("hello")(parsed)
      print(res) should === (expectedRes11)
    }

    "add attribute in root element" in {
      val parsed = parse(input10)

      val res = root.attrs.modify(attrs => attrs :+ Attribute.unprefixed("anotherKey", "newValue"))(parsed)
      print(res) should === (expectedRes12)
    }

    "replaceOrAddAttr in root element" in {
      {
        val parsed = parse(input13)
        val res = root.replaceOrAddAttr("anotherKey", "newValue")(parsed)
        print(res) should === (expectedRes12)
      }
      {
        val parsed = parse(input14)
        val res = root.replaceOrAddAttr("anotherKey", "newValue")(parsed)
        print(res) should === (expectedRes12)
      }
    }

    // TODO: add to cookbook
    "renameLabel" in {
      val parsed = parse(example15("f"))

      val res = (root \ "c1").renameLabel("f", "xyz")(parsed)
      print(res) should === (example15("xyz"))
    }

    // TODO: add sht like this to cookbook
    "having" in {
      import LabeledElementOptics._
      import NodeOptics._

      val parsed = parse(example15("f"))

      // TODO: does not look nice
      val res = (((root \ "c1").having { node =>
        isLabeledElement.composeOptional(isLabeled("g")).getOption(node).isDefined
      }) \ "f").hasTextOnly.modify(_.toUpperCase)(parsed)

      print(res) should === (output16)
    }

    "having 2" in {
      import LabeledElementOptics._
      import NodeOptics._
      import net.michalsitko.xml.optics.ElementOptics._

      val parsed = parse(example17("item"))

      val res = (((root \ "c1").having { node =>
        isLabeledElement.composeOptional(isLabeled("g")).composeOptional(attribute("someKey")).getOption(node).isDefined
      }) \ "f").hasTextOnly.modify(_.toUpperCase)(parsed)

      print(res) should === (example17("ITEM"))
    }

    // TODO: add info to cookbook, comment difference with another index methods (in optics)
    "index" in {
      val parsed = parse(example17("item"))

      val res = (root \ "c1" \ "f").index(1).hasTextOnly.modify(_.toUpperCase)(parsed)

      print(res) should === (example17("ITEM"))
    }

    "index and then index" in {
      val parsed = parse(example19("item"))

      val res = ((root \ "c1" \ "f").index(1) \ "h" \ "i").index(1).hasTextOnly.modify(_.toUpperCase)(parsed)

      print(res) should === (example19("ITEM"))
    }

    "childAt" in {
      // we need to minimize as unneccessary Text nodes (caused just by the fact that input string is formatted)
      // would interfere with `childAt`
      val parsed = parse(example18("item")).minimize

      val res = (root \ "c1" \ "f").childAt(1).hasTextOnly.modify(_.toUpperCase)(parsed)

      print(res)(PrinterConfig(Indent.IndentWith("  "), true)) should === (example18("ITEM"))
    }

    "elementAt" in {
      // contrary to `childAt` we don't need to minimize as Text elements will be ignored
      val parsed = parse(example18("item"))

      val res = (root \ "c1" \ "f").elementAt(1).hasTextOnly.modify(_.toUpperCase)(parsed)

      print(res) should === (example18("ITEM"))
    }

    // TODO: may be a nice addition to cookbook
    "insert new node as the first node" in {
      val parsed = parse(minimalInput)

      // TODO: better factory methods needed instead of this
      val newElement = LabeledElement.unprefixed("new", Element())
      val res = (root \ "f").children.modify( ch => newElement +: ch)(parsed)

      print(res)(PrinterConfig(Indent.IndentWith("  "), true)) should === (example20)
    }

    "insert new node as the last node" in {
      val parsed = parse(minimalInput)

      // TODO: better factory methods needed instead of this
      val newElement = LabeledElement.unprefixed("new", Element())
      val res = (root \ "f").children.modify( ch => ch :+ newElement)(parsed)

      print(res)(PrinterConfig(Indent.IndentWith("  "), true)) should === (example21)
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

  val input5 =
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

  val input6 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns:a="http://a.com" xmlns:b="http://b.com">
      |   <c1>
      |      <f a:someKey="oldValue">item1</f>
      |      <f someKey="oldValue">item2</f>
      |   </c1>
      |   <c1>
      |      <a:f someKey="oldValue">item1</a:f>
      |      <f b:someKey="oldValue">item2</f>
      |      <b:f someKey="oldValue">item2</b:f>
      |   </c1>
      |</a>""".stripMargin

  // why for <a:f> new attribute is added instead of being modified is not obvious, for explanation look at:
  // https://stackoverflow.com/questions/41561/xml-namespaces-and-attributes
  val expectedRes6 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns:a="http://a.com" xmlns:b="http://b.com">
      |   <c1>
      |      <f a:someKey="newValue">item1</f>
      |      <f xmlns:a="http://a.com" someKey="oldValue" a:someKey="newValue">item2</f>
      |   </c1>
      |   <c1>
      |      <a:f xmlns:a="http://a.com" someKey="oldValue" a:someKey="newValue">item1</a:f>
      |      <f xmlns:a="http://a.com" b:someKey="oldValue" a:someKey="newValue">item2</f>
      |      <b:f xmlns:a="http://a.com" someKey="oldValue" a:someKey="newValue">item2</b:f>
      |   </c1>
      |</a>""".stripMargin

  val input7 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns:a="http://a.com" xmlns:b="http://b.com">
      |   <c1>
      |      <f a:someKey="oldValue">item1</f>
      |      <f someKey="oldValue">item2</f>
      |      <f b:someKey="oldValue">item3</f>
      |      <f anotherKey="oldValue">item4</f>
      |      <f>item5</f>
      |   </c1>
      |</a>""".stripMargin

  val expectedRes7 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns:a="http://a.com" xmlns:b="http://b.com">
      |   <c1>
      |      <f a:someKey="OLDVALUE">item1</f>
      |      <f someKey="oldValue">item2</f>
      |      <f b:someKey="oldValue">item3</f>
      |      <f anotherKey="oldValue">item4</f>
      |      <f>item5</f>
      |   </c1>
      |</a>""".stripMargin

  val expectedRes8 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns:a="http://a.com" xmlns:b="http://b.com">
      |   <c1>
      |      <f a:someKey="OLDVALUE">item1</f>
      |      <f someKey="OLDVALUE">item2</f>
      |      <f b:someKey="OLDVALUE">item3</f>
      |      <f anotherKey="oldValue">item4</f>
      |      <f>item5</f>
      |   </c1>
      |</a>""".stripMargin

  val expectedRes9 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns:a="http://a.com" xmlns:b="http://b.com">
      |   <c1>
      |      <f a:someKey="oldValue">item1</f>
      |      <f someKey="OLDVALUE">item2</f>
      |      <f b:someKey="oldValue">item3</f>
      |      <f anotherKey="oldValue">item4</f>
      |      <f>item5</f>
      |   </c1>
      |</a>""".stripMargin

  val input10 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a someKey="oldValue">
      |</a>""".stripMargin

  val expectedRes10 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a someKey="newValue">
      |</a>""".stripMargin

  val expectedRes11 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a someKey="oldValue">hello</a>""".stripMargin

  val expectedRes12 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a someKey="oldValue" anotherKey="newValue">
      |</a>""".stripMargin

  val input13 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a someKey="oldValue">
      |</a>""".stripMargin

  val input14 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a someKey="oldValue" anotherKey="oldValue">
      |</a>""".stripMargin

  def example15(toReplace: String) =
    s"""<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <$toReplace>item</$toReplace>
      |      <g>item</g>
      |   </c1>
      |   <c2>
      |      <f>item</f>
      |   </c2>
      |   <c1>
      |      <$toReplace>item</$toReplace>
      |   </c1>
      |</a>""".stripMargin

  val output16 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>ITEM</f>
      |      <g>item</g>
      |   </c1>
      |   <c2>
      |      <f>item</f>
      |   </c2>
      |   <c1>
      |      <f>item</f>
      |   </c1>
      |</a>""".stripMargin

  def example17(toReplace: String) =
    s"""<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item</f>
      |      <g>item</g>
      |   </c1>
      |   <c1>
      |      <f>$toReplace</f>
      |      <g someKey="someValue">item</g>
      |   </c1>
      |</a>""".stripMargin

  def example18(toReplace: String) =
    s"""<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |  <c1>
      |    <f></f>
      |    <f>
      |      <h>abc</h>
      |    </f>
      |    <f>
      |      <h>abc</h>
      |      <i>$toReplace</i>
      |    </f>
      |    <f>
      |      <h>abc</h>
      |      <i>$toReplace</i>
      |      <i>abc</i>
      |    </f>
      |  </c1>
      |</a>""".stripMargin

  val input =
    s"""<?xml version="1.0" encoding="UTF-8"?>
       |<a>
       |  <f></f>
       |  <f>
       |    <h>abc</h>
       |    <i>toReplace</i>
       |  </f>
       |  <f>
       |    <h>abc</h>
       |    <i>toReplace</i>
       |  </f>
       |</a>""".stripMargin

  def example19(toReplace: String) =
    s"""<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |  <c1>
      |    <f>
      |      <h>
      |        <i>item</i>
      |        <i>to be selected</i>
      |      </h>
      |    </f>
      |    <f>
      |      <h>
      |        <i>item</i>
      |        <i>$toReplace</i>
      |      </h>
      |    </f>
      |  </c1>
      |</a>""".stripMargin

  val minimalInput =
    s"""<?xml version="1.0" encoding="UTF-8"?>
       |<a>
       |  <f>
       |    <g>some text</g>
       |  </f>
       |</a>""".stripMargin

  val example20 =
    s"""<?xml version="1.0" encoding="UTF-8"?>
       |<a>
       |  <f>
       |    <new></new>
       |    <g>some text</g>
       |  </f>
       |</a>""".stripMargin

  val example21 =
    s"""<?xml version="1.0" encoding="UTF-8"?>
       |<a>
       |  <f>
       |    <g>some text</g>
       |    <new></new>
       |  </f>
       |</a>""".stripMargin

}
