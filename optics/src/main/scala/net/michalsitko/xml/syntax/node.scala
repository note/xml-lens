package net.michalsitko.xml.syntax

import net.michalsitko.xml.entities.LabeledElement
import net.michalsitko.xml.optics.NodeOps

trait ToNodeOps {
  implicit def toNodeOps(node: LabeledElement): NodeOps =
    new NodeOps(node)
}

object node extends ToNodeOps
