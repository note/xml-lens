package net.michalsitko

import scala.xml._

object Hello extends AnyRef with XmlFragments with XmlSupport {
  def main(args: Array[String]): Unit = {
    println("Hello, world!")

    val xml: Elem = XML.loadString(asString)

    val res = transformNaive(xml)
    println(s"transformNaive: $res")
  }

  def transformNaive(root: Elem): NodeSeq = {
    def replace: PartialFunction[Node, Node] = {
      case el: Elem =>
        println("bazinga 1")
        el.copy(child = List(Text("f replaced")))
      case el =>
        println("bazinga 2")
        el
    }

    root.map {
      deeper("a")(deeper("b")(deeper("c")(deeper("d")(deeper("e1")(update("f")(replace))))))
    }.head.map {
      deeper("a")(deeper("b")(deeper("c")(deeper("d")(deeper("e2")(update("f")(replace))))))
    }.head

//    val res = for {
//      elemB <- (root \ "b")
//      elemC <- (elemB \ "c")
//      elemD <- (elemC \ "d")
//    } yield {
////      (elemB, elemC, elemD) match {
////        case (b: Elem, c: Elem, d: Elem) =>
//          val dChanged = (elemD.child).flatMap {
//            // we are lucky that we can treat e1 and e2 the same
//            case e: Elem if (e.label == "e1" || e.label == "e2") =>
//              println("bazinga e: " + e)
//              (e \ "_").map(replaceElem("f")(e => e.copy(child = {println("ss: " + e); List(Text("f replaced")) })))
//            case e =>
//              println("othwerise: " + e)
//              e
//          }
//          println("koniec")
//          val cChanged = (elemC.child).flatMap {
//            case e: Elem if(e.label == "d") => dChanged
//            case e =>
//              println("bazingaaa: " + e)
//              e
//          }
//          elemB.map(_.copy(child = cChanged.head))
////      }
//    }
//    res
  }

  private def replaceElem(label: String)(fn: Elem => NodeSeq): PartialFunction[Node, NodeSeq] = {
    case e: Elem if (e.label == label) =>
      fn(e)
    case e =>
      println("bazinga zle: " + e)
      e
  }
}

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
