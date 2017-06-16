package net.michalsitko.xml.test.utils

import net.michalsitko.xml.entities.{Element, LabeledElement, ResolvedName, Text}

import scala.annotation.tailrec

trait XmlGenerator {
  def elementOfDepth(depth: Int): LabeledElement = {
    @tailrec
    def loop(n: Int, child: LabeledElement): LabeledElement = {
      if (n == 0) {
        child
      } else {
        val current = LabeledElement(ResolvedName.unprefixed("abcd"), Element(List.empty, List(child), List.empty))
        loop(n - 1, current)
      }
    }

    loop(depth - 1, LabeledElement(ResolvedName.unprefixed("abcd"), Element(List.empty, List(Text("some text")), List.empty)))
  }
}
