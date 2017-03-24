package net.michalsitko

import scala.collection.immutable.Seq
import scala.xml._

object Hello extends AnyRef with XmlFragments with XmlSupport {
  def main(args: Array[String]): Unit = {

    // let's start with simpleAsString
    val simpleXml: Elem = XML.loadString(simpleAsString)

    // let's do it naively:
    val res1 = simpleXml.map {
      case aElem: Elem if (aElem.label == "a") =>
        aElem.copy(child = aElem.child.flatMap {
          case c1Elem: Elem if (c1Elem.label == "c1") =>
            c1Elem.copy(child = c1Elem.child.flatMap {
              case fElem: Elem if (fElem.label == "f") =>
                fElem.copy(child = List(Text("f replaced")))
              case el => el
            })
          case el => el
        })
      case el => el
    }
    println(res1)

    // as we see it does not scales well
    // also some pattern emerges
    // let rewrite it with XmlSupport trait:
    val res2 = simpleXml.map {
      deeper("a")(deeper("c1")(update("f"){
        case elem: Elem => elem.copy(child = List(Text("f replaced")))
      }))
    }.head
    println(res2)

    // more complicated XML:
    val anotherXml = XML.loadString(asString)

    // this one scales nicer:
    val res3 = anotherXml.map {
      deeper("a")(deeper("b")(deeper("c")(deeper("d")(deeper("e1")(update("f"){
        case elem: Elem => elem.copy(child = List(Text("f replaced")))
      })))))
    }.head
    println(res3)

    // let's change `f` node also inside node `e2`:
    def replaceF: PartialFunction[Node, Node] = {
      case elem: Elem => elem.copy(child = List(Text("f replaced")))
    }
    val res4 = anotherXml.map {
      deeper("a")(deeper("b")(deeper("c")(deeper("d")(deeper("e1")(update("f")(replaceF))))))
    }.head.map {
      deeper("a")(deeper("b")(deeper("c")(deeper("d")(deeper("e2")(update("f")(replaceF))))))
    }
    println(res4)
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

  val simpleAsString =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c2>
      |      <f>item1</f>
      |      <g>item2</g>
      |      <h>item3</h>
      |   </c2>
      |   <s>summary</s>
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
