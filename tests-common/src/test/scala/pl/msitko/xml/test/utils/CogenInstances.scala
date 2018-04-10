package pl.msitko.xml.test.utils

import pl.msitko.xml.entities.{Attribute, Element, LabeledElement, Node}
import org.scalacheck.Cogen

trait CogenInstances {
  implicit val labeledElemCogen = Cogen[LabeledElement]((_: LabeledElement).hashCode().toLong)
  implicit val elementCogen     = Cogen[Element]((_: Element).hashCode().toLong)
  implicit val nodesCogen       = Cogen[Seq[Node]]((_: Seq[Node]).hashCode().toLong)
  implicit val nodeCogen        = Cogen[Node]((_: Node).hashCode().toLong)
  implicit val attributeCogen   = Cogen[Attribute]((_: Attribute).hashCode().toLong)
}
