package net.michalsitko.parsing

import net.michalsitko.entities.{Element, _}
import net.michalsitko.parsing.utils.ExampleInputs
import org.scalatest.{Matchers, WordSpec}

class XmlParserSpec extends WordSpec with Matchers with ExampleInputs {
  "XmlParser" should {
    "return proper Element for XML without any namespaces declared and with no whitespaces" in {
      val res = XmlParser.parse(noNamespaceXmlString)

      val expectedTree = Element(resolvedName("a"), details(List(
        Element(resolvedName("c1"), details(List(
          Element(resolvedName("f"), details(List(Text("item1")))),
          Element(resolvedName("g"), details(List(Text("item2"))))
        ))),
        Element(resolvedName("c1"), details(List(
          Element(resolvedName("f"), details(List(Text("item1")))),
          Element(resolvedName("h"), details(List(Text("item2"))))
        )))
      )))
      res should equal(Right(expectedTree))
    }

    "return proper Element for XML without any namespace and some whitespaces" in {
      val res = XmlParser.parse(noNamespaceXmlStringWithWs)

      val expectedTree = Element(resolvedName("a"), details(List(
        indent(1),
        Element(resolvedName("c1"), details(List(
          indent(2),
          Element(resolvedName("f"), details(List(Text("item1")))),
          indent(2),
          Element(resolvedName("g"), details(List(Text("item2")))),
          indent(1)
        ))),
        indent(1),
        Element(resolvedName("c1"), details(List(
          indent(2),
          Element(resolvedName("f"), details(List(Text("item1")))),
          indent(2),
          Element(resolvedName("h"), details(List(Text("item2")))),
          indent(1)
        ))),
        Text(lineBreak)
      )))
      res should equal(Right(expectedTree))
    }

    "return proper Element for XML with some namespaces declared" in {
      val res = XmlParser.parse(namespaceXmlString)

      def defaultNs = "http://www.develop.com/student"
      def anotherNs = "http://www.example.com"

      val expectedTree = Element(ResolvedName("", Some(defaultNs), "a"), Details(Seq.empty, List(
        indent(1),
        Element(ResolvedName("", Some(defaultNs), "c1"), details(List(
          indent(2),
          Element(ResolvedName("", Some(defaultNs), "f"), details(List(Text("item1")))),
          indent(2),
          Element(ResolvedName("", Some(defaultNs), "g"), details(List(Text("item2")))),
          indent(1)
        ))),
        indent(1),
        Element(ResolvedName("", Some(defaultNs), "c1"), details(List(
          indent(2),
          Element(ResolvedName("", Some(defaultNs), "f"), details(List(Text("item1")))),
          indent(2),
          Element(ResolvedName("xyz", Some(anotherNs), "h"), details(List(Text("item2")))),
          indent(1)
        ))),
        Text(lineBreak)
      ), List(NamespaceDeclaration(None, "http://www.develop.com/student"), NamespaceDeclaration(Some("xyz"), "http://www.example.com"))))
      res should equal(Right(expectedTree))
    }

    "parse attributes" in {
      val res = XmlParser.parse(attributesXmlString)

      val fAttributes = List(Attribute("", None, "name", "abc"), Attribute("", None, "name2", "something else"))
      val c1Attributes = List(Attribute("", None, "name", ""))
      val expectedTree = Element(resolvedName("a"), details(List(
        Element(resolvedName("c1"), details(List(
          Element(resolvedName("f"), Details(fAttributes, List(Text("item1")), Seq.empty)),
          Element(resolvedName("g"), details(List(Text("item2"))))
        ))),
        Element(resolvedName("c1"), Details(c1Attributes, List(
          Element(resolvedName("f"), details(List(Text("item1")))),
          Element(resolvedName("h"), details(List(Text("item2"))))
        ), Seq.empty))
      )))
      res should equal(Right(expectedTree))
    }

    "parse attributes with namespaces" in {
      val res = XmlParser.parse(attributesWithNsXmlString)

      val defaultNs = "http://www.a.com"
      val bNs = "http://www.b.com"

      // https://stackoverflow.com/questions/41561/xml-namespaces-and-attributes
      val fAttributes = List(Attribute("", None, "name", "abc"), Attribute("b", Some(bNs), "attr", "attr1"))
      val gAttributes = List(Attribute("b", Some(bNs), "name", "def"))
      val hAttributes = List(Attribute("", None, "name", "ghi"))
      val expectedTree = Element(ResolvedName("", Some(defaultNs), "a"), Details(Seq.empty, List(
        Element(ResolvedName("", Some(defaultNs), "c1"), details(List(
          Element(ResolvedName("", Some(defaultNs), "f"), Details(fAttributes, List(Text("item1")), Seq.empty)),
          Element(ResolvedName("", Some(defaultNs), "g"), Details(gAttributes, List(Text("item2")), Seq.empty)),
          Element(ResolvedName("b", Some(bNs), "h"), Details(hAttributes, List(Text("item3")), Seq.empty))
        )))
      ), List(NamespaceDeclaration(None, "http://www.a.com"), NamespaceDeclaration(Some("b"), "http://www.b.com"))))
      res should equal(Right(expectedTree))
    }


    "fail for malformed inputs" in {
      XmlParser.parse(malformedXmlString).isLeft should equal(true)
      XmlParser.parse(malformedXmlString2).isLeft should equal(true)
    }
  }

  val lineBreak = System.getProperty("line.separator")
  val indent = " " * 3
  val lineBreakWithIndent = s"$lineBreak$indent"

  private def details(children: Seq[Node]): Details = {
    Details(Seq.empty, children, Seq.empty)
  }

  def indent(level: Int): Text = Text(lineBreak + (indent * level))

  def resolvedName(name: String) = ResolvedName("", None, name)
}
