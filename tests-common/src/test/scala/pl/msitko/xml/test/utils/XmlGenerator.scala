package pl.msitko.xml.test.utils

import pl.msitko.xml.entities._
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
