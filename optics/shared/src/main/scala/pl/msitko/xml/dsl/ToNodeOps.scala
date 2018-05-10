package pl.msitko.xml.dsl

import pl.msitko.xml.entities.LabeledElement

trait ToNodeOps {
  implicit def toNodeOps(node: LabeledElement): NodeOps =
    new NodeOps(node)
}
