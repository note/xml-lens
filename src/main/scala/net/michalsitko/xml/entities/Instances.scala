package net.michalsitko.xml.entities

import scalaz.{Applicative, Equal}

object Instances {
  implicit val labeledElementEq: Equal[LabeledElement] =
    (l1: LabeledElement, l2: LabeledElement) => l1 == l2

  implicit val elementEq: Equal[Element] =
    (e1: Element, e2: Element) => e1 == e2
}
