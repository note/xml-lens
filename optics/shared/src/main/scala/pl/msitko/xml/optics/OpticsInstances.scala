package pl.msitko.xml.optics

import monocle.Traversal
import monocle.function.Plated
import pl.msitko.xml.entities._

trait OpticsInstances {
  implicit val nodePlated: Plated[Node] = new Plated[Node] {
    override def plate: Traversal[Node, Node] = NodeOptics.nodeToNodeTraversal
  }
}

object OpticsInstances extends OpticsInstances
