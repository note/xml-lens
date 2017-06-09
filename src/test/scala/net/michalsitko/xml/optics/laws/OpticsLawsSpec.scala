package net.michalsitko.xml.optics.laws

import monocle.law.discipline.{OptionalTests, TraversalTests}
import net.michalsitko.xml.optics.Optics
import net.michalsitko.xml.utils.{ArbitraryInstances, CogenInstances}
import org.scalacheck.Arbitrary
import org.scalatest.prop.Checkers
import org.scalatest.{FlatSpec, Matchers}
import org.typelevel.discipline.Laws

class OpticsLawsSpec extends OpticsSpec with Matchers with ArbitraryInstances with CogenInstances {
  import net.michalsitko.xml.entities.Instances._

  import scalaz.std.string._

  implicit val arbLabeledElem = Arbitrary(labeledElementGen(4, Some("abc")))
  implicit val arbElem = Arbitrary(labeledElementGen(2, None).map(_.element))

  val deepTest    = TraversalTests(Optics.deep("abc"))
  val deeperTest  = TraversalTests(Optics.deeper("abc"))
  // TODO: does it test any positive situation?
  val hasTextOnlyTest = OptionalTests(Optics.hasTextOnly)
  // TODO: does it test anything (there are not "abc" attributes ...)
  val attributeTest = OptionalTests(Optics.attribute("abc"))

  checkLaws("deep Traversal", deepTest)
  checkLaws("deeper Traversal", deeperTest)
  checkLaws("hasTextOnly Optional", hasTextOnlyTest)
  checkLaws("attribute Optional", deeperTest)

}

trait OpticsSpec extends FlatSpec {
  def checkLaws(name: String, ruleSet: Laws#RuleSet, maxSize: Int = 100): Unit = {
//    val configParams = List(Checkers.MinSuccessful(15), Checkers.SizeRange(PosZInt.from(maxSize).get))

    ruleSet.all.properties.zipWithIndex.foreach {
      case ((id, prop), 0) => name should s"obey $id" in Checkers.check(prop)
      case ((id, prop), _) => it should s"obey $id" in Checkers.check(prop)
    }
  }
}
