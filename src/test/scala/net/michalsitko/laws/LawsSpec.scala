package net.michalsitko.laws

import monocle.internal.IsEq
import monocle.law.{LensLaws, PrismLaws}
import net.michalsitko.optics.Optics2
import net.michalsitko.utils.XmlFragments
import org.scalatest.matchers.{MatchResult, Matcher}
import org.scalatest.{Matchers, WordSpec}

import scala.xml._

class LawsSpec extends WordSpec with Matchers with IsEqMatchers {
  val elem: Elem = XML.loadString(XmlFragments.simpleAsString)

  "elemPrism" should {
    "work" in {
      val prismLaws = PrismLaws(Optics2.elemPrism)

      check(prismLaws.partialRoundTripOneWay(new Text("abc")))
      check(prismLaws.roundTripOtherWay(elem))

      check(prismLaws.modifyIdentity(new Text("abc")))
      check(prismLaws.composeModify(new Text("abc"), identity, identity))

      check(prismLaws.consistentSetModify(new Text("abc"), elem))
      check(prismLaws.consistentModifyModifyId(new Text("abc"), identity))
      check(prismLaws.consistentGetOptionModifyId(new Text("abc")))
    }
  }

  "lens" should {
    "work" in {
      val lensLaws = LensLaws(Optics2.nodeLens("c1"))

      check(lensLaws.getSet(elem))

      // TODO: generally, the most interesting cases are those we are setting back lens with NodeSeq of size different than earlier (before setting)
      check(lensLaws.setGet(elem, NodeSeq.Empty))
      check(lensLaws.setIdempotent(elem, NodeSeq.Empty))
      check(lensLaws.modifyIdentity(elem))
      check(lensLaws.composeModify(elem, identity, identity))
      check(lensLaws.consistentSetModify(elem, NodeSeq.Empty))
      check(lensLaws.consistentModifyModifyId(elem, identity))
      check(lensLaws.consistentGetModifyId(elem))
    }
  }

  private def check[T](isEq: IsEq[T]): Unit = {
    assert(isEq.lhs == isEq.rhs)
  }
}

trait IsEqMatchers {
  class Work[T] extends Matcher[IsEq[T]] {
    override def apply(isEq: IsEq[T]): MatchResult = {
      MatchResult(isEq.lhs == isEq.rhs, s"${isEq.lhs} does not equal ${isEq.rhs}", "s\"${isEq.lhs} equals ${isEq.rhs}\"")
    }
  }

  def work = new Work
}
