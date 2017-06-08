package net.michalsitko.printing

import net.michalsitko.entities._
import net.michalsitko.utils.ExampleInputs
import org.scalatest.{Matchers, WordSpec}

class XmlPrinterSpec extends WordSpec with Matchers with ExampleInputs {
  "XmlPrinter" should {
    "work" in {
      val input = Element(resolvedName("a"), details(List(
        Element(resolvedName("c1"), details(List(
          Element(resolvedName("f"), details(List(Text("item1")))),
          Element(resolvedName("g"), details(List(Text("item2"))))
        ))),
        Element(resolvedName("c1"), details(List(
          Element(resolvedName("f"), details(List(Text("item1")))),
          Element(resolvedName("h"), details(List(Text("item2"))))
        )))
      )))

      val res = XmlPrinter.print(input)
      res should equal (noNamespaceXmlString)
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
