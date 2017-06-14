package net.michalsitko.xml.optics

import monocle.Traversal
import monocle.function.Plated
import net.michalsitko.xml.entities._

trait OpticsInstances {
  implicit val nodePlated: Plated[Node] = new Plated[Node] {
    override def plate: Traversal[Node, Node] = Optics.nodeToNodeTraversal
  }
}

object OpticsInstances extends OpticsInstances
