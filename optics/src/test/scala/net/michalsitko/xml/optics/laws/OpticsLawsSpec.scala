package net.michalsitko.xml.optics.laws

import monocle.law.discipline.{LensTests, OptionalTests, TraversalTests}
import net.michalsitko.xml.entities.Node
import net.michalsitko.xml.optics.ElementOptics
import net.michalsitko.xml.test.utils.{ArbitraryElementConfig, ArbitraryInstances, CogenInstances}
import org.scalacheck.{Arbitrary, Gen}
import org.scalactic.anyvals.PosZInt
import org.scalatest.prop.Checkers
import org.scalatest.{FlatSpec, Matchers}
import org.typelevel.discipline.Laws

class OpticsLawsSpec extends LawsSpec with Matchers with ArbitraryInstances with CogenInstances {
  import net.michalsitko.xml.entities.Instances._
  import net.michalsitko.xml.optics.ElementOptics._
  import net.michalsitko.xml.optics.LabeledElementOptics._
  import net.michalsitko.xml.optics.NodeOptics._

  import scalaz.std.string._

  implicit val arbLabeledElem =
    Arbitrary(labeledElementGen(ArbitraryElementConfig(4, 4, Some("abc"), None)))
  implicit val arbNode = Arbitrary(arbLabeledElem.arbitrary.map(_.asInstanceOf[Node]))
  implicit val arbNodes = Arbitrary(Gen.listOf(arbNode.arbitrary).map(_.toSeq))
  implicit val arbElem =
    Arbitrary(labeledElementGen(ArbitraryElementConfig(1, 2, None, Some("someAttr"))).map(_.element))
  implicit val arbAttr = Arbitrary(attributeGen(Some("someAttr")))

  val deepTest          = TraversalTests(deep("abc"))
  val deeperTest        = TraversalTests(deeper("abc"))
  val nodeTraversalTest = TraversalTests(nodeToNodeTraversal)
  val hasTextOnlyTest   = OptionalTests(hasTextOnly)
  val attributeTest     = OptionalTests(attribute("someAttr"))
  val hasOneChildTest   = OptionalTests(hasOneChild)
  val attributesTest    = LensTests(attributes)
  val childrenTest      = LensTests(ElementOptics.children)
//  val hasChildLabeledTest = PrismTests(hasChildLabeled("abc"))

  checkLaws("deep Traversal", deepTest)
  checkLaws("deeper Traversal", deeperTest)
  checkLaws("nodeTraversalTest Traversal", nodeTraversalTest)
  checkLaws("hasTextOnly Optional", hasTextOnlyTest)
  checkLaws("attribute Optional", attributeTest)
  checkLaws("hasOneChild Optional", hasOneChildTest)
  checkLaws("attributes Lens", attributesTest)
  // TODO: investigate why tests are slow with default value for maxSize
  checkLaws("children Lens", childrenTest, 8)
//  checkLaws("hasChildLabeled Prism", hasChildLabeledTest)

}

trait LawsSpec extends FlatSpec {
  def checkLaws(name: String, ruleSet: Laws#RuleSet, maxSize: Int = 100): Unit = {
    val configParams = List(Checkers.MinSuccessful(20), Checkers.SizeRange(PosZInt.from(maxSize).get))

    ruleSet.all.properties.zipWithIndex.foreach {
      case ((id, prop), 0) => name should s"obey $id" in Checkers.check(prop, configParams:_*)
      case ((id, prop), _) => it should s"obey $id" in Checkers.check(prop, configParams:_*)
    }
  }
}
