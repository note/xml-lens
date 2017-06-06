package net.michalsitko.laws

import monocle.internal.IsEq
import monocle.law.discipline.LensTests
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
import scalaz.Equal

class LawsSpec extends OpticsSpec with Matchers with ArbitraryInstances with CogenInstances {
  implicit val nodeEqualInstance: Equal[Node] = (n1: Node, n2: Node) => n1 == n2
  implicit val nodeSeqEqualInstance: Equal[NodeSeq] = (n1: NodeSeq, n2: NodeSeq) => n1 == n2


  val elem: Elem = XML.loadString(XmlFragments.simpleAsString)

  implicit val arbNode = Arbitrary[Node](elemWithLabelOccurance(4, "abc"))
  implicit val arbNodeSeq = Arbitrary(nodeSeq(2))

  val nodeLens = LensTests(Optics.nodeLens("abc"))



  checkLaws("nodeLens", nodeLens)

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
