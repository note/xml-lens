package pl.msitko.xml.syntax

import pl.msitko.xml.entities.LabeledElement
import pl.msitko.xml.optics.NodeOps

trait ToNodeOps {
  implicit def toNodeOps(node: LabeledElement): NodeOps =
    new NodeOps(node)
}

object node extends ToNodeOps
