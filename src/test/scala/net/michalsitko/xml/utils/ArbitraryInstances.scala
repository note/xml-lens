package net.michalsitko.xml.utils

import net.michalsitko.xml.entities._
import org.scalacheck.Gen

import scala.collection.JavaConverters._

trait ArbitraryInstances {
  private val maxNumberOfAttributes = 3
  private val maxChildren = 3
  private val randomLabelProbability = 9

  def labeledElementGen(desiredDepth: Int, label: Option[String]): Gen[LabeledElement] = {
    def loop(depth: Int): Gen[List[LabeledElement]] = {
      if (depth == 0) {
        Gen.choose(1, maxChildren).flatMap(n => Gen.listOfN(n, singleLabeledElementGen(List(Text("Hello XML")), label)))
      } else {
        for {
          childrenNumber: Int <- Gen.choose(1, maxChildren)
          allChildren <- Gen.sequence(List.fill(childrenNumber)(loop(depth - 1)))
          currentNodes <- Gen.sequence(allChildren.asScala.toList.map(children => singleLabeledElementGen(children, label)))
        } yield currentNodes.asScala.toList
      }
    }

    loop(desiredDepth - 1).map(_.head)
  }

  def singleLabeledElementGen(children: List[Node], label: Option[String]): Gen[LabeledElement] = {
    val labelGen = label match {
      case Some(desiredLabel) =>
        Gen.frequency[String]((randomLabelProbability, alphanumGen), (1, Gen.const(desiredLabel)))
      case None =>
        alphanumGen
    }

    for {
      localName <- labelGen
      label     = ResolvedName.unprefixed(localName)
      element   <- singleElementGen(children)
    } yield LabeledElement(label, element)
  }

  private def singleElementGen(children: List[Node]): Gen[Element] = {
    for {
      attrs <- attributesGen
    } yield Element(attrs, children, Seq.empty)
  }


  // TODO: may be not the best way
  private val alphanumGen: Gen[String] = Gen.alphaStr.map(_.take(9) + "a")

  val attributeGen = for {
    attrName <- alphanumGen
    attrVal  <- alphanumGen
  } yield Attribute.unprefixed(attrName, attrVal)

  val attributesGen = Gen.choose(0, maxNumberOfAttributes).flatMap(n =>  Gen.listOfN(n, attributeGen))

}
