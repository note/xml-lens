package pl.msitko.xml.optics.laws

import monocle.law.discipline.TraversalTests
import pl.msitko.xml.entities.Node
import pl.msitko.xml.test.utils.ArbitraryElementConfig
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.Matchers
import pl.msitko.xml.test.utils.{ArbitraryElementConfig, ArbitraryInstances, CogenInstances}

class NodeOpticsLawsSpec extends LawsSpec with Matchers with ArbitraryInstances with CogenInstances {
  import pl.msitko.xml.entities.Instances._
  import pl.msitko.xml.optics.NodeOptics._

  implicit val arbNode = {
    val arbLabeledElem = Arbitrary(labeledElementGen(ArbitraryElementConfig(4, 4, Some("abc"), None)))
    Arbitrary(arbLabeledElem.arbitrary.map(_.asInstanceOf[Node]))
  }
  implicit val arbNodes = Arbitrary(Gen.listOf(arbNode.arbitrary).map(_.toSeq))

  val nodeTraversalTest = TraversalTests(nodeToNodeTraversal)

  checkLaws("nodeTraversalTest Traversal", nodeTraversalTest)
}
