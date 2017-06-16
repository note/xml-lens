package net.michalsitko.xml.test.utils

import net.michalsitko.xml.entities._
import org.scalacheck.Gen

import scala.collection.JavaConverters._

case class ArbitraryElementConfig(minDepth: Int, maxDepth: Int, label: Option[String], attributeKey: Option[String])

trait ArbitraryInstances {
  private val maxNumberOfAttributes = 3
  private val maxChildren = 3
  private val randomLabelProbability = 9
  private val randomAttributeProbability = 9

  // TODO: may be not the best way
  private val alphanumGen: Gen[String] = Gen.alphaStr.map(_.take(9) + "a")

  private def attributeGen(desiredAttributeKey: Option[String]): Gen[Attribute] = {
    val attrNameGen = desiredAttributeKey match {
      case Some(attrKey) =>
        Gen.frequency[String]((randomAttributeProbability, alphanumGen), (1, Gen.const(attrKey)))
      case None =>
        alphanumGen
    }
    for {
      attrName <- attrNameGen
      attrVal  <- alphanumGen
    } yield Attribute.unprefixed(attrName, attrVal)
  }

  private def attributesGen(desiredAttributeKey: Option[String]): Gen[Seq[Attribute]] = {
    def uniqueAttrsKeys(attrs: List[Attribute]): Seq[Attribute] = {
      attrs.foldLeft(Vector.empty[Attribute]){ (acc, curr) =>
        if(acc.forall(!_.sameKey(curr.key))) {
          acc :+ curr
        } else {
          acc
        }
      }
    }

    for {
      attrsNum  <- Gen.choose(0, maxNumberOfAttributes)
      attrs     <- Gen.listOfN(attrsNum, attributeGen(desiredAttributeKey))
    } yield uniqueAttrsKeys(attrs)
  }

  private val textGen = alphanumGen.map(Text(_))
  private val leafNodeGen: Gen[List[Node]] = Gen.oneOf(textGen.map(List(_)), Gen.const(List.empty[Node]))

  def labeledElementGenWithDepth(desiredDepth: Int, cfg: ArbitraryElementConfig): Gen[LabeledElement] = {
    def loop(depth: Int): Gen[List[LabeledElement]] = {
      if (depth == 0) {
        Gen.choose(1, maxChildren).flatMap(n => Gen.listOfN(n, singleLabeledElementGen(leafNodeGen, cfg)))
      } else {
        for {
          childrenNumber: Int <- Gen.choose(1, maxChildren)
          allChildren <- Gen.sequence(List.fill(childrenNumber)(loop(depth - 1)))
          currentNodes <- Gen.sequence(allChildren.asScala.toList.map(children => singleLabeledElementGen(children, cfg)))
        } yield currentNodes.asScala.toList
      }
    }

    loop(desiredDepth - 1).map(_.head)
  }

  def labeledElementGen(config: ArbitraryElementConfig): Gen[LabeledElement] =
    for {
      depth <- Gen.choose(config.minDepth, config.maxDepth)
      elem  <- labeledElementGenWithDepth(depth, config)
    } yield elem

  def singleLabeledElementGen(childrenGen: Gen[List[Node]], cfg: ArbitraryElementConfig): Gen[LabeledElement] = {
    val labelGen = cfg.label match {
      case Some(desiredLabel) =>
        Gen.frequency[String]((randomLabelProbability, alphanumGen), (1, Gen.const(desiredLabel)))
      case None =>
        alphanumGen
    }

    for {
      localName <- labelGen
      label     = ResolvedName.unprefixed(localName)
      children  <- childrenGen
      element   <- singleElementGen(children, cfg.attributeKey)
    } yield LabeledElement(label, element)
  }

  private def singleElementGen(children: List[Node], desiredAttributeKey: Option[String]): Gen[Element] = {
    for {
      attrs <- attributesGen(desiredAttributeKey)
    } yield Element(attrs, children, Seq.empty)
  }

}
