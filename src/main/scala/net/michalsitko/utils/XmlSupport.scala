package net.michalsitko.utils

import scala.xml.{Elem, Node, NodeSeq}

trait XmlSupport {
  def deeper(toReplace: String)(fn: PartialFunction[Node, NodeSeq]): PartialFunction[Node, NodeSeq] = {
    case elem: Elem if (elem.label == toReplace) =>
      elem.copy(child = elem.child.flatMap(fn))
    case e =>
      e
  }

  def update(toReplace: String)(fn: PartialFunction[Node, Node]): PartialFunction[Node, NodeSeq] = {
    case elem: Elem if (elem.label == toReplace) =>
      elem.map(fn)
    case e => e
  }

  def extract(parent: NodeSeq, fieldName: String): Option[String] = {
    (parent \ fieldName).headOption.map(_.text)
  }

  def getAttr(parent: NodeSeq, attrName: String): Option[String] = {
    parent.headOption.flatMap(_.headOption).flatMap(_.attribute(attrName)).map(_.text)
  }
}
