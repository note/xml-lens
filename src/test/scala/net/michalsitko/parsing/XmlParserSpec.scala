package net.michalsitko.parsing

import net.michalsitko.entities.{Details, Element, Node, Text}
import net.michalsitko.parsing.utils.ExampleInputs
import org.scalatest.{Matchers, WordSpec}

class XmlParserSpec extends WordSpec with Matchers with ExampleInputs {
  "XmlParser" should {
    "return proper Element for XML without any namespaces declared and with no whitespaces" in {
      val res = XmlParser.parse(noNamespaceXmlString)

      val expectedTree = Element("a", details(List(
        Element("c1", details(List(
          Element("f", details(List(Text("item1")))),
          Element("g", details(List(Text("item2"))))
        ))),
        Element("c1", details(List(
          Element("f", details(List(Text("item1")))),
          Element("h", details(List(Text("item2"))))
        )))
      )))
      res should equal(Right(expectedTree))
    }

    "return proper Element for XML without any namespace and some whitespaces" in {
      val res = XmlParser.parse(noNamespaceXmlStringWithWs)

      val expectedTree = Element("a", details(List(
        indent(1),
        Element("c1", details(List(
          indent(2),
          Element("f", details(List(Text("item1")))),
          indent(2),
          Element("g", details(List(Text("item2")))),
          indent(1)
        ))),
        indent(1),
        Element("c1", details(List(
          indent(2),
          Element("f", details(List(Text("item1")))),
          indent(2),
          Element("h", details(List(Text("item2")))),
          indent(1)
        ))),
        Text(lineBreak)
      )))
      res should equal(Right(expectedTree))
    }

    "return proper Element for XML with some namespaces declared" in {
      XmlParser.parse(xmlString).isRight should equal(true)
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
}
