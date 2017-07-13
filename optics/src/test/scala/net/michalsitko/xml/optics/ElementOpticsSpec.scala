package net.michalsitko.xml.optics

import net.michalsitko.xml.entities.{LabeledElement, Text}
import net.michalsitko.xml.test.utils.ExampleBuilderHelper
import org.scalatest.{Matchers, WordSpec}

class ElementOpticsSpec extends WordSpec with Matchers with ExampleBuilderHelper {
  import ElementOptics._

  "indexOptional" should {
    "work" in {
      indexOptional(2).getOption(input.element) should equal (Some(expected))
    }

    "be able to set text Node" in {
      val expectedOutput: LabeledElement =
        labeledElement("a",
          Text("NEW"),
          labeledElement("b",
            Text("world"),
            Text("abc")
          ),
          expected
        )

      indexOptional(0).set(Text("NEW"))(input.element) should equal (expectedOutput.element)
    }

    "return None if node with given index does not exist" in {
      indexOptional(3).getOption(input.element) should equal (None)
    }
  }

  "indexElementOptional" should {
    "work" in {
      indexElementOptional(1).getOption(input.element) should equal (Some(expected))
    }

    "return None if element with given index does not exist" in {
      indexElementOptional(2).getOption(input.element) should equal (None)
    }
  }

  val expected =
    labeledElement("c",
      labeledElement("d",
        Text("def")
      )
    )

  val input: LabeledElement =
    labeledElement("a",
      Text("hello"),
      labeledElement("b",
        Text("world"),
        Text("abc")
      ),
      expected
    )
}
