package net.michalsitko.utils

import org.scalacheck.{Arbitrary, Gen}

import scala.xml._
import scala.collection.JavaConverters._

trait ArbitraryInstances {
  def arbitraryNode(depth: Int): Arbitrary[Node] = ???

  // TODO: Not sure if retryUnit is the way to go
  val alphanumGen: Gen[String] = Gen.alphaStr.map(_.take(10)).retryUntil(_.nonEmpty)

  val attrGen = for {
    attrName <- alphanumGen
    attrVal  <- alphanumGen
  } yield new UnprefixedAttribute(attrName, new Text(attrVal), Null)

  private val maxAttrsLength = 2

  val attrsGen = for {
    length      <- Gen.choose(0, maxAttrsLength)
    attributes  <- Gen.listOfN(length, attrGen)
  } yield attributes

  val linkedAttrs: Gen[MetaData] = attrsGen.map { listOfAttrs =>
    listOfAttrs.foldLeft[MetaData](Null){ (prev, current) =>
      new UnprefixedAttribute(current.key, current.value, prev)
    }
  }

  def leafElem(children: List[Node]): Gen[Elem] = for {
    label <- alphanumGen
    attrs <- linkedAttrs
  } yield newElem(label, attrs, children)

  private val maxDirectChilden = 3

  def elemOfDepth(depth: Int): Gen[Elem] = {
    // TODO: non-tail recursion
    def loop(depth: Int): Gen[List[Elem]] = {
      if(depth == 0) {
        Gen.choose(1, maxDirectChilden).flatMap(n => Gen.listOfN(n, leafElem(List(Text("Hello XML")))))
      } else {
        for {
          newChildrenNumber <- Gen.choose(1, maxDirectChilden)
          children          <- Gen.sequence(List.fill(newChildrenNumber)(loop(depth - 1)))
          currentNodes      <- Gen.sequence(children.asScala.toList.map(ch => leafElem(ch)))
          currentNodesList  <- currentNodes.asScala.toList
        } yield currentNodesList
      }
    }

    loop(depth - 1).map(_.head)
  }

  def elem(depth: Int) = {
    elemOfDepth(depth)
  }

  def newElem(label: String, attributes: MetaData, children: List[Node]) =
    Elem(prefix = null, label = label, attributes = attributes, scope = TopScope,
      minimizeEmpty = false, child = children:_*)
}

object ArbitraryInstances extends ArbitraryInstances
