package net.michalsitko.xml.test.utils

import net.michalsitko.xml.entities._

trait ExampleBuilderHelper {
  lazy val lineBreak = System.getProperty("line.separator")
  lazy val indent = " " * 3
  lazy val lineBreakWithIndent = s"$lineBreak$indent"

  def element(children: Node*): Element =
    Element(children = children)

  def indent(level: Int): Text = Text(lineBreak + (indent * level))

  def labeledElement(name: String, children: Node*) =
    LabeledElement.unprefixed(name, element(children:_*))
}
