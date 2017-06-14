package net.michalsitko.xml.utils

import net.michalsitko.xml.entities._

trait ExampleBuilderHelper {
  lazy val lineBreak = System.getProperty("line.separator")
  lazy val indent = " " * 3
  lazy val lineBreakWithIndent = s"$lineBreak$indent"

  def element(children: Node*): Element = {
    Element(Seq.empty, children, Seq.empty)
  }

  def indent(level: Int): Text = Text(lineBreak + (indent * level))

  def resolvedName(name: String) = ResolvedName("", None, name)

  def labeledElement(name: String, children: Node*) =
    LabeledElement(resolvedName(name), element(children:_*))
}
