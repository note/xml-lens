package net.michalsitko.utils

import java.util

import org.scalacheck.{Arbitrary, Gen}

import scala.annotation.tailrec
import scala.xml._
import scala.collection.JavaConverters._

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

//  def elemOfDepth(depth: Int): Gen[Elem] = {
//    def loop(inside: Gen[Elem], depth: Int, siblingsN: Int): Gen[List[Elem]] = {
//      if(depth == 0) {
//        Gen.choose(1, maxDirectChilden).flatMap(n => Gen.listOfN(n, inside))
//      } else {
//        for {
//          label <- alphanumGen
//          attrs <- linkedAttrs
//          inner <- loop(inside, depth - 1)
//        } yield List(newElem(label, attrs, inner))
//      }
//    }
//    loop(leafElem(List(Text("Hello XML"))), depth).map(_.head)
//  }

  def elemOfDepth(depth: Int): Gen[Elem] = {
    @tailrec
    def loop(depth: Int, parents: Gen[List[Elem]], children: Gen[List[List[Elem]]]): Gen[List[Elem]] = {
      if(depth == 0) {
        for {
          parentList <- parents
          childrenList <- children
        } yield parentList.zip(childrenList).map {
          case (parent, children) => parent.copy(child = children)
        }
      } else {
        println(s"bazinga $depth")
        val abc: Gen[(List[Elem], util.ArrayList[List[Elem]])] = for {
          parentList <- parents
          childrenList <- children
          newChildrenNumber <- Gen.choose(1, maxDirectChilden)
          newParents = parentList.zip(childrenList).map {
            case (parent, children) =>
              parent.copy(child = children)
          }
          newChildren = List.fill(newParents.size)(Gen.listOfN(newChildrenNumber, leafElem(List(Text("Hello XML")))))
          nn <- Gen.sequence(newChildren)
        } yield (newParents, nn)

        val args: Gen[(List[Elem], List[List[Elem]])] = abc.map(t => (t._1, t._2.asScala.toList))
        loop(depth - 1, args.map(_._1), args.map(_._2))
      }
    }

    val tmp: Gen[List[Elem]] = for {
      newChildrenNumber <- Gen.choose(1, maxDirectChilden)
      ch <- Gen.listOfN(newChildrenNumber, leafElem(List(Text("Hello XML"))))
    } yield ch
    loop(depth, leafElem(List.empty).map(List(_)), tmp.map(ch => List(ch))).map(_.head)
  }

  def elem(depth: Int) = {
    def loop(elem: Elem): Elem = {
      elem.par
    }
  }

  def newElem(label: String, attributes: MetaData, children: List[Node]) =
    Elem(prefix = null, label = label, attributes = attributes, scope = TopScope,
      minimizeEmpty = false, child = children:_*)
}

object ArbitraryInstances extends ArbitraryInstances
