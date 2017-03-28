package net.michalsitko

import net.michalsitko.utils.{XmlFragments, XmlSupport}

import scala.collection.immutable.Seq
import scala.xml._
import scalaz.Lens

object WrongOptics1 extends AnyRef with XmlFragments with XmlSupport with Optics {
  def main(args: Array[String]): Unit = {

    // let's start with simpleAsString
    val simpleXml: Elem = XML.loadString(verySimpleString)

//    simpleXml.map {
//      case a => println("map: " + a)
//    }
//
//    simpleXml.child.map {
//      case a => println("child.map: " + a)
//    }
//
//    (simpleXml \ "c1").map {
//      case a => println("(/ c1) map: " + a)
//    }

    // let's do it naively:
//    val res1 = simpleXml.map {
//      case aElem: Elem if (aElem.label == "a") =>
//        aElem.copy(child = aElem.child.flatMap {
//          case c1Elem: Elem if (c1Elem.label == "c1") =>
//            c1Elem.copy(child = c1Elem.child.flatMap {
//              case fElem: Elem if (fElem.label == "f") =>
//                fElem.copy(child = List(Text("f replaced")))
//              case el => el
//            })
//          case el => el
//        })
//      case el => el
//    }
//    println(res1)
//
//    // as we see it does not scales well
//    // also some pattern emerges
//    // let rewrite it with XmlSupport trait:
//    val res2 = simpleXml.map {
//      deeper("a")(deeper("c1")(update("f"){
//        case elem: Elem => elem.copy(child = List(Text("f replaced")))
//      }))
//    }.head
//    println(res2)
//
//    // more complicated XML:
//    val anotherXml = XML.loadString(asString)
//
//    // this one scales nicer:
//    val res3 = anotherXml.map {
//      deeper("a")(deeper("b")(deeper("c")(deeper("d")(deeper("e1")(update("f"){
//        case elem: Elem => elem.copy(child = List(Text("f replaced")))
//      })))))
//    }.head
//    println(res3)
//
//    // let's change `f` node also inside node `e2`:
//    def replaceF: PartialFunction[Node, Node] = {
//      case elem: Elem => elem.copy(child = List(Text("f replaced")))
//    }
//    val res4 = anotherXml.map {
//      deeper("a")(deeper("b")(deeper("c")(deeper("d")(deeper("e1")(update("f")(replaceF))))))
//    }.head.map {
//      deeper("a")(deeper("b")(deeper("c")(deeper("d")(deeper("e2")(update("f")(replaceF))))))
//    }.head
//    println(res4)

    // now with lenses:

    // it somehow works but only for verySimpleString.
    // Run it with simpleAsString to see that it does not work properly for more complicated cases.
    val lensRes = {
//      val aLens = lens("a")
      val c1Lens = lens("c1")
      val fLens = lens("f")
      val wholeLens = (c1Lens >=> fLens)
      println("res of get: " + wholeLens.get(simpleXml))
      println("res -------------")
      val res2 = wholeLens.set(simpleXml, List(Text("f replaced")))
      println("res2: " + res2)
    }
//
//    println("more complicated example with lenses")
//    val lensRes2 = {
//      val wholeLens = lens("a") >=> lens("b") >=> lens("c") >=> lens("d") >=> lens("e1") >=> lens("f")
//      wholeLens %= (el => el.map)
    // probably will need to use `%=` method of lens, see at http://eed3si9n.com/learning-scalaz/Lens.html
//    }
  }
}

trait Optics {
  def lens(label: String): Lens[NodeSeq, NodeSeq] = Lens.lensu[NodeSeq, NodeSeq] (
    (element, newValue) => {
      element.map {
        case parent: Elem =>
          parent.copy(child = parent.child.flatMap {
            case root: Elem if root.label == label =>
              println("bazinga 100: " + label)
              root.flatMap(_ => newValue)
            case el => el
          })
        case a => a
      }
    },
    element => {
      element \ label
    }
  )
}
