package net.michalsitko.xml.entities

import scalaz.Equal

object Instances {
  implicit val labeledElementEq: Equal[LabeledElement] = new Equal[LabeledElement] {
    override def equal(l1: LabeledElement, l2: LabeledElement): Boolean = l1 == l2
  }

  implicit val elementEq: Equal[Element] = new Equal[Element] {
    override def equal(e1: Element, e2: Element): Boolean = e1 == e2
  }
}
