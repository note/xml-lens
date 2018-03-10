package pl.msitko.xml.optics

import monocle.Traversal
import monocle.function.Plated
import pl.msitko.xml.entities._

trait OpticsInstances {
  implicit val nodePlated: Plated[Node] = new Plated[Node] {
    override def plate: Traversal[Node, Node] = NodeOptics.nodeToNodeTraversal
  }

  implicit val labeledElementPlated: Plated[LabeledElement] = new Plated[LabeledElement] {
    override def plate: Traversal[LabeledElement, LabeledElement] = LabeledElementOptics.labeledElementTraversal
  }
}

object OpticsInstances extends OpticsInstances
