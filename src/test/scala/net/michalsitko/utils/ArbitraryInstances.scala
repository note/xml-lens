package net.michalsitko.utils

import org.scalacheck.{Arbitrary, Gen}

import scala.xml._

trait ArbitraryInstances {
  def arbitraryNode(depth: Int): Arbitrary[Node] = ???

  val alphanumGen: Gen[String] = Gen.alphaNumStr.map(_.take(10))

  val attrGen = for {
    attrName <- alphanumGen
    attrVal  <- alphanumGen
  } yield new UnprefixedAttribute(attrName, new Text(attrVal), Null)

  private val maxAttrsLength = 5

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

  private val maxDirectChilden = 5

  def elemOfDepth(depth: Int): Gen[Elem] = {
    def loop(inside: Gen[Elem], depth: Int): Gen[List[Elem]] = {
      if(depth == 0) {
        Gen.choose(1, maxDirectChilden).flatMap(n => Gen.listOfN(n, inside))
      } else {
        for {
          label <- alphanumGen
          attrs <- linkedAttrs
          inner <- loop(inside, depth - 1)
        } yield List(newElem(label, attrs, inner))
      }
    }
    loop(leafElem(List(Text("Hello XML"))), depth).map(_.head)
  }

  def elem(depth: Int) = elemOfDepth(depth)

  def newElem(label: String, attributes: MetaData, children: List[Node]) =
    Elem(prefix = null, label = label, attributes = attributes, scope = TopScope,
      minimizeEmpty = false, child = children:_*)
}

object ArbitraryInstances extends ArbitraryInstances
