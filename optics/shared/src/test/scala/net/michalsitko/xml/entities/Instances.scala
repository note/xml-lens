package net.michalsitko.xml.entities

import scalaz.Equal

object Instances {
  implicit val labeledElementEq: Equal[LabeledElement] = new Equal[LabeledElement] {
    override def equal(l1: LabeledElement, l2: LabeledElement): Boolean = l1 == l2
  }

  implicit val elementEq: Equal[Element] = new Equal[Element] {
    override def equal(e1: Element, e2: Element): Boolean = e1 == e2
  }

  implicit val nodeEq: Equal[Node] = new Equal[Node] {
    override def equal(n1: Node, n2: Node): Boolean = n1 == n2
  }

  implicit def seqEq[T : Equal]: Equal[Seq[T]] = new Equal[Seq[T]] {
    override def equal(n1: Seq[T], n2: Seq[T]): Boolean = n1.zip(n2).forall(t => Equal[T].equal(t._1, t._2))
  }

  implicit val attrEq: Equal[Attribute] = new Equal[Attribute] {
    override def equal(a1: Attribute, a2: Attribute): Boolean = a1 == a2
  }
}
