package net.michalsitko.xml.optics.laws

import monocle.law.discipline.TraversalTests
import net.michalsitko.xml.optics.Optics
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.utils.{ArbitraryInstances, CogenInstances}
import org.scalacheck.Arbitrary
import org.scalactic.anyvals.PosZInt
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.Checkers
import org.typelevel.discipline.Laws

class OpticsLawsSpec extends OpticsSpec with Matchers with ArbitraryInstances with CogenInstances {
  import net.michalsitko.xml.entities.Instances._

  implicit val arbLabeledElem = Arbitrary(labeledElementGen(4, Some("abc")))
  implicit val arbElem = Arbitrary(labeledElementGen(2, None).map(_.element))

//  val samples = for {
//    i <- 0 until 10
//  } yield gen.sample.get
//  samples.foreach(s => println(XmlPrinter.print(s)))

  val deepTest = TraversalTests(Optics.deep("abc"))
  checkLaws("deep Traversal", deepTest)

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
