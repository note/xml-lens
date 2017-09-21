package net.michalsitko.xml.test.utils

import net.michalsitko.xml.entities.{Element, LabeledElement, Text}

import scala.annotation.tailrec

trait XmlGenerator {
  def elementOfDepth(depth: Int): LabeledElement = {
    @tailrec
    def loop(n: Int, child: LabeledElement): LabeledElement = {
      if (n == 0) {
        child
      } else {
        val current = LabeledElement.unprefixed("abcd", Element(children = List(child)))
        loop(n - 1, current)
      }
    }

    loop(depth - 1, LabeledElement.unprefixed("abcd", Element(children = List(Text("some text")))))
  }
}
