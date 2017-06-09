package net.michalsitko.xml.optics.laws

import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.utils.ArbitraryInstances
import org.scalactic.anyvals.PosZInt
import org.scalatest.{FlatSpec, Matchers}
import org.scalatest.prop.Checkers
import org.typelevel.discipline.Laws

class OpticsLawsSpec extends FlatSpec with Matchers with ArbitraryInstances {
  val gen = labeledElementGen(4, Some("abc"))

  val samples = for {
    i <- 0 until 10
  } yield gen.sample.get

  samples.foreach(s => println(XmlPrinter.print(s)))

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
