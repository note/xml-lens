package net.michalsitko.xml.utils

import net.michalsitko.xml.entities.{Element, Node}
import org.scalacheck.Cogen

trait CogenInstances {
  implicit val elementCogen = Cogen[Element]((_: Element).hashCode().toLong)
  implicit val nodesCogen = Cogen[Seq[Node]]((_: Seq[Node]).hashCode().toLong)
}
