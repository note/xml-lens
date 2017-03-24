package net.michalsitko

import scala.xml._

object Hello extends AnyRef with XmlFragments {
  def main(args: Array[String]): Unit = {
    println("Hello, world!")

    val xml: Elem = XML.loadString(asString)

    val res = transformNaive(xml)
    println(s"transformNaive: $res")
  }

  def transformNaive(root: Elem): Elem = {
//    root.map {
//      deeper("b")(deeper("c"))
//    }

    val res = for {
      elemB <- (root \ "b")
      elemC <- (elemB \ "c")
      elemD <- (elemC \ "d")
      elemE1 <- (elemD \ "e1")
      elemE2 <- (elemD \ "e2")
    } yield {
      (elemB, elemC, elemD) match {
        case (b: Elem, c: Elem, d: Elem) =>
          val e1Changed = (elemE1 \ "f").map {
            case elem: Elem if (elem.label == "f") =>
              elem.copy(child = List(Text("f replaced")))
            case e =>
              e
          }
          val e2Changed = (elemE2 \ "f").map {
            case elem: Elem if (elem.label == "f") =>
              elem.copy(child = List(Text("f replaced")))
            case e => e
          }
          b.copy(child = c.copy(child = d.copy(child = e1Changed ++ e2Changed)))
      }
    }
    res.head.asInstanceOf[Elem]
  }
}

//trait XmlSupport {
//  def deeper(toReplace: String)(fn: PartialFunction[Node, NodeSeq]): PartialFunction[Node, NodeSeq] = {
//    case elem: Elem if (elem.label == toReplace) =>
//      elem.copy(child = elem.child.flatMap(fn))
//    case e =>
//      e
//  }
//
//  def update(toReplace: String)(fn: PartialFunction[Node, Node]): PartialFunction[Node, NodeSeq] = {
//    case elem: Elem if (elem.label == toReplace) =>
//      elem.map(fn)
//    case e => e
//  }
//
//  def addAttr[T: XmlPrintable](name: String, value: T) =
//    Attribute(None, name, Text(implicitly[XmlPrintable[T]].print(value)), Null)
//
//  def addAttrIfPresent[T: XmlPrintable](name: String, value: Option[T]) =
//    value.map(v => Attribute(None, name, Text(implicitly[XmlPrintable[T]].print(v)), Null)).getOrElse(Null)
//
//  def extract(parent: NodeSeq, fieldName: String): Option[String] = {
//    (parent \ fieldName).headOption.map(_.text)
//  }
//
//  def getAttr(parent: NodeSeq, attrName: String): Option[String] = {
//    parent.headOption.flatMap(_.headOption).flatMap(_.attribute(attrName)).map(_.text)
//  }
//}

trait XmlFragments {
  val asString =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |  <b>
      |    <c>
      |      <d>
      |        <e1>
      |          <f>item1</f>
      |          <g>item2</g>
      |        </e1>
      |        <e2>
      |          <f>item1</f>
      |          <g>item2</g>
      |          <h>item3</h>
      |        </e2>
      |      </d>
      |      <s>summary</s>
      |    </c>
      |  </b>
      |</a>
    """.stripMargin

  val asLiteral =
    <a>
      <b>
        <c>
          <d>
            <e1>
              <f>item1</f>
              <g>item2</g>
            </e1>
            <e2>
              <f>item1</f>
              <g>item2</g>
              <h>item3</h>
            </e2>
          </d>
          <s>summary</s>
        </c>
      </b>
    </a>
}
