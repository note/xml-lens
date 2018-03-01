package pl.msitko.xml.optics.laws

import monocle.law.discipline.{OptionalTests, TraversalTests}
import pl.msitko.xml.test.utils.ArbitraryElementConfig
import org.scalacheck.Arbitrary
import org.scalatest.Matchers
import pl.msitko.xml.test.utils.{ArbitraryElementConfig, ArbitraryInstances, CogenInstances}

class LabeledElementOpticsLawsSpec extends LawsSpec with Matchers with ArbitraryInstances with CogenInstances {
  import pl.msitko.xml.entities.Instances._
  import pl.msitko.xml.optics.LabeledElementOptics._

  implicit val arbLabeledElem =
    Arbitrary(labeledElementGen(ArbitraryElementConfig(4, 4, Some("abc"), None)))
  implicit val arbElem =
    Arbitrary(labeledElementGen(ArbitraryElementConfig(1, 2, None, Some("someAttr"))).map(_.element))

  val deepTest = TraversalTests(deep("abc"))
  val isLabeledTest = OptionalTests(isLabeled("abc"))

  checkLaws("deep Traversal", deepTest)
  checkLaws("isLabeled Optional", isLabeledTest)
}

