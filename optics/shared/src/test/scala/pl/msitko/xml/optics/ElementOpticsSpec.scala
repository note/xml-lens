package pl.msitko.xml.optics

import pl.msitko.xml.entities.{LabeledElement, Node, Text}
import pl.msitko.xml.test.utils.{BaseSpec, ExampleBuilderHelper}

class ElementOpticsSpec extends BaseSpec with ExampleBuilderHelper {
  import ElementOptics._

  "indexOptional" should {
    "return the n-th node" in {
      indexOptional(0).getOption(input.element) should === (Some(first))
      indexOptional(1).getOption(input.element) should === (Some(second))
      indexOptional(2).getOption(input.element) should === (Some(third))
    }

    "return None if node with given index does not exist" in {
      indexOptional(3).getOption(input.element) should === (None)
      indexOptional(105).getOption(input.element) should === (None)
    }

    "be able to set text Node" in {
      val expectedOutput = testedElement(Text("NEW"), second, third).element

      indexOptional(0).set(Text("NEW"))(input.element) should === (expectedOutput)
    }
  }

  "indexElementOptional" should {
    "work" in {
      indexElementOptional(1).getOption(input.element) should === (Some(third))
    }

    "return None if element with given index does not exist" in {
      indexElementOptional(2).getOption(input.element) should === (None)
    }
  }


  val first = Text("hello")
  val second =
    labeledElement("b",
      Text("world"),
      Text("abc")
    )
  val third =
    labeledElement("c",
      labeledElement("d",
        Text("def")
      )
    )

  val input = testedElement(first, second, third)

  def testedElement(firstNode: Node, secondNode: Node, thirdNode: Node): LabeledElement =
    labeledElement("a", children = firstNode, secondNode, thirdNode)
}
