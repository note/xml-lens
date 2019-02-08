package pl.msitko.xml.optics.laws

import monocle.law.discipline.{LensTests, OptionalTests, PrismTests, TraversalTests}
import org.scalacheck.Arbitrary
import org.scalatest.Matchers
import pl.msitko.xml.entities.Node
import pl.msitko.xml.test.utils.{ArbitraryElementConfig, ArbitraryInstances, CogenInstances}

class ElementOpticsLawsSpec extends LawsSpec with Matchers with ArbitraryInstances with CogenInstances {
  import pl.msitko.xml.entities.Instances._
  import pl.msitko.xml.optics.ElementOptics._

  import scalaz.std.string._

  val cfg = ArbitraryElementConfig(1, 4, Some("abc"), Some("someAttr"))

  val commonGen = labeledElementGen(cfg)
  implicit val arbLabeledElem = Arbitrary(commonGen)
  implicit val arbNode = Arbitrary(commonGen.map(l => l : Node))
  implicit val arbElem = Arbitrary(commonGen.map(_.element))
  implicit val arbAttr = Arbitrary(attributeGen(Some("someAttr")))


//  for {
//    _ <- 0 until 15
////    _ = println(arbElem.arbitrary.sample.get)
//    _ = println(arbElem.arbitrary.sample.get.attributes.map(_.key.localName).contains("someAttr"))
//  } yield ()


  val deeperTest              = TraversalTests(deeper("abc"))
  val allLabeledElementsTest  = TraversalTests(allLabeledElements)
  val hasTextOnlyTest         = OptionalTests(hasTextOnly)
  val attributeTest           = OptionalTests(attribute("someAttr"))
  val hasOneChildTest         = OptionalTests(hasOneChild)
  val attributesTest          = LensTests(attributes)
  val childrenTest            = LensTests(children)
  val beingTest               = PrismTests(being(_.attributes.map(_.key.localName).contains("someAttr")))

  checkLaws("Traversal laws for deeper", deeperTest)
  checkLaws("Traversal laws for allLabeledElements", allLabeledElementsTest)
  checkLaws("Optional laws for hasTextOnly", hasTextOnlyTest)
  checkLaws("Optional laws for attribute", attributeTest)
  checkLaws("Optional laws for hasOneChild", hasOneChildTest)
  checkLaws("Lens laws for attributes", attributesTest)
  // TODO: investigate why tests are slow with default value for maxSize
  checkLaws("Lens laws for children", childrenTest, 8)
  checkLaws("Prism laws for being", beingTest)
}

