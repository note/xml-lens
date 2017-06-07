package net.michalsitko.laws

import cats.data.NonEmptyList
import monocle.internal.IsEq
import monocle.law.discipline.{LensTests, OptionalTests}
import monocle.law.{LensLaws, PrismLaws}
import net.michalsitko.optics.Optics
import net.michalsitko.utils.{ArbitraryInstances, CogenInstances, XmlFragments}
import org.scalacheck.Arbitrary
import org.scalactic.anyvals.PosZInt
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.prop.Checkers
import org.scalatest.{FlatSpec, Matchers, WordSpec}
import org.typelevel.discipline.Laws

import scala.xml._
import scala.xml.Utility
import scalaz.Equal

class LawsSpec extends OpticsSpec with Matchers with ArbitraryInstances with CogenInstances {
//  implicit val nodeEqualInstance: Equal[Node] = (n1: Node, n2: Node) => n1 == n2
//  implicit val nodeSeqEqualInstance: Equal[NodeSeq] = (n1: NodeSeq, n2: NodeSeq) => n1 == n2

  implicit val elemEqual: Equal[Elem] = (e1: Elem, e2: Elem) => {
    // TODO: not sure it's proper. Better than `e1 == e2` which has a problem with whitespaces
    Utility.trim(e1) == Utility.trim(e2)
  }
  implicit val nonEmptyListOfElemsEqual: Equal[NonEmptyList[Elem]] = {
    (l1: NonEmptyList[Elem], l2: NonEmptyList[Elem]) =>
      val elemEq = implicitly[Equal[Elem]]
      val (list1, list2) = (l1.toList, l2.toList)
      list1.size == list2.size && list1.zip(list2).forall(t => elemEq.equal(t._1, t._2))
  }


  val elem: Elem = XML.loadString(XmlFragments.simpleAsString)

//  implicit val arbNode = Arbitrary[Node](elemWithLabelOccurance(4, "abc"))
//  implicit val arbNodeSeq = Arbitrary(nodeSeq(2))
  implicit val arbElem  = Arbitrary[Elem](elemWithLabelOccurance(2, "abc"))
  implicit val arbElems = arbNonEmptyListOfElems(arbElem)

//  val nodeLens = LensTests(Optics.nodeLens("abc"))
  val elemTest = OptionalTests(Optics.elem("abc"))

  checkLaws("elem Optional", elemTest)

}

trait OpticsSpec extends FlatSpec {
  def checkLaws(name: String, ruleSet: Laws#RuleSet, maxSize: Int = 100): Unit = {
    val configParams = List(Checkers.MinSuccessful(15), Checkers.SizeRange(PosZInt.from(maxSize).get))

    ruleSet.all.properties.zipWithIndex.foreach {
      case ((id, prop), 0) => name should s"obey $id" in Checkers.check(prop, configParams:_*)
      case ((id, prop), _) => it should s"obey $id" in Checkers.check(prop, configParams:_*)
    }
  }
}
