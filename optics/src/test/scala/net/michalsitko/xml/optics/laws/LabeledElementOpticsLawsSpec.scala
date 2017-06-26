package net.michalsitko.xml.optics.laws

import monocle.law.discipline.TraversalTests
import net.michalsitko.xml.test.utils.{ArbitraryElementConfig, ArbitraryInstances, CogenInstances}
import org.scalacheck.Arbitrary
import org.scalatest.Matchers

class LabeledElementOpticsLawsSpec extends LawsSpec with Matchers with ArbitraryInstances with CogenInstances {
  import net.michalsitko.xml.entities.Instances._
  import net.michalsitko.xml.optics.LabeledElementOptics._

  implicit val arbLabeledElem =
    Arbitrary(labeledElementGen(ArbitraryElementConfig(4, 4, Some("abc"), None)))
  implicit val arbElem =
    Arbitrary(labeledElementGen(ArbitraryElementConfig(1, 2, None, Some("someAttr"))).map(_.element))

  val deepTest = TraversalTests(deep("abc"))

  checkLaws("deep Traversal", deepTest)
}

