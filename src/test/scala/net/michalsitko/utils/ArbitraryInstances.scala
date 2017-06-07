package net.michalsitko.utils

import cats.data.NonEmptyList
import org.scalacheck.{Arbitrary, Gen}

import scala.xml._
import scala.collection.JavaConverters._

trait ArbitraryInstances {
  def arbitraryNode(depth: Int): Arbitrary[Node] = ???

  def elemWithLabelOccurance(depth: Int, label: String): Gen[Elem] = {
    elemOfDepth(depth, Some(label))
  }

  def nodeSeq(depth: Int): Gen[NodeSeq] = {
    Gen.choose(0, 2).flatMap(n => Gen.listOfN(n, elemOfDepth(depth, None))).map(NodeSeq.fromSeq(_))
  }

  def arbNonEmptyListOfElems(arbElem: Arbitrary[Elem]): Arbitrary[NonEmptyList[Elem]] =
    Arbitrary(arbElem.arbitrary.flatMap(Gen.nonEmptyListOf(_).map(NonEmptyList.fromListUnsafe(_))))

  private val maxAttrsLength = 2
  private val maxDirectChilden = 3

  // TODO: Not sure if retryUntil is the way to go
  private val alphanumGen: Gen[String] = Gen.alphaStr.map(_.take(9) + "a")

  private val attrGen = for {
    attrName <- alphanumGen
    attrVal  <- alphanumGen
  } yield new UnprefixedAttribute(attrName, new Text(attrVal), Null)

  private val attrsGen = for {
    length      <- Gen.choose(0, maxAttrsLength)
    attributes  <- Gen.listOfN(length, attrGen)
  } yield attributes

  private val linkedAttrs: Gen[MetaData] = attrsGen.map { listOfAttrs =>
    listOfAttrs.foldLeft[MetaData](Null){ (prev, current) =>
      new UnprefixedAttribute(current.key, current.value, prev)
    }
  }

  private def elemOfDepth(desiredDepth: Int, label: Option[String]): Gen[Elem] = {
    // TODO: non-tail recursion
    def loop(depth: Int): Gen[List[Elem]] = {
      if(depth == 0) {
        Gen.choose(1, maxDirectChilden).flatMap(n => Gen.listOfN(n, leafElem(List(Text("Hello XML")), label)))
      } else {
        val usedLabel = if(depth - 1 == desiredDepth) None else label
        for {
          newChildrenNumber <- Gen.choose(1, maxDirectChilden)
          children          <- Gen.sequence(List.fill(newChildrenNumber)(loop(depth - 1)))
          currentNodes      <- Gen.sequence(children.asScala.toList.map(ch => leafElem(ch, usedLabel)))
          currentNodesList  <- currentNodes.asScala.toList
        } yield currentNodesList
      }
    }

    loop(desiredDepth - 1).map(_.head)
  }

  private def leafElem(children: List[Node], label: Option[String]): Gen[Elem] = {
    val labelGen = label match {
      case Some(desiredLabel) =>
        Gen.frequency[String]((9, alphanumGen), (1, Gen.const(desiredLabel)))
      case None =>
        alphanumGen
    }

    for {
      label <- labelGen
      attrs <- linkedAttrs
    } yield newElem(label, attrs, children)
  }

  private def newElem(label: String, attributes: MetaData, children: List[Node]) =
    Elem(prefix = null, label = label, attributes = attributes, scope = TopScope,
      minimizeEmpty = false, child = children:_*)
}

object ArbitraryInstances extends ArbitraryInstances
