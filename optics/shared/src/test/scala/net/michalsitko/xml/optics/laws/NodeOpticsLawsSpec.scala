package net.michalsitko.xml.optics.laws

import monocle.law.discipline.TraversalTests
import net.michalsitko.xml.entities.Node
import net.michalsitko.xml.test.utils.{ArbitraryElementConfig, ArbitraryInstances, CogenInstances}
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.Matchers

class NodeOpticsLawsSpec extends LawsSpec with Matchers with ArbitraryInstances with CogenInstances {
  import net.michalsitko.xml.entities.Instances._
  import net.michalsitko.xml.optics.NodeOptics._

  implicit val arbNode = {
    val arbLabeledElem = Arbitrary(labeledElementGen(ArbitraryElementConfig(4, 4, Some("abc"), None)))
    Arbitrary(arbLabeledElem.arbitrary.map(_.asInstanceOf[Node]))
  }
  implicit val arbNodes = Arbitrary(Gen.listOf(arbNode.arbitrary).map(_.toSeq))

  val nodeTraversalTest = TraversalTests(nodeToNodeTraversal)

  checkLaws("nodeTraversalTest Traversal", nodeTraversalTest)
}
