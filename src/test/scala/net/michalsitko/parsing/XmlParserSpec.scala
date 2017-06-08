package net.michalsitko.parsing

import net.michalsitko.entities._
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

      val expectedTree = Element(ResolvedName("", Some(defaultNs), "a"), details(List(
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
      )))
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
