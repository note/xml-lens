package net.michalsitko.optics

import monocle.{Lens, Prism, Traversal}

import scala.xml.{Elem, Node, NodeSeq}
import scalaz.{Applicative, Traverse}
import scalaz.std.list._
import scala.collection.immutable

object Instances {
  type N[A] = NodeSeq
  new Traverse[N] {
    override def traverseImpl[G[_], A, B](fa: N[A])(f: (A) => G[B])(implicit evidence$1: Applicative[G]): G[N[B]] = ???
  }
}

trait XmlOptics {
  // it will not work because of NodeSeq:
//  lazy val toElem = Prism.partial[NodeSeq, Elem] {case el: Elem => el}(identity)
//  lazy val toElem = Prism.partial[Node, Elem] {case el: Elem => el}(identity)
//  lazy val eachNode = Traversal.fromTraverse[immutable.Seq, Int]

//  lazy val toElem = Prism[NodeSeq, Elem](
//    _.collectFirst { case el: Elem => el }
//  )(el => NodeSeq.fromSeq(Seq(el)))


  def deeper(label: String) = Lens[Elem, NodeSeq] { el =>
    val res = el \ label
    println(s"get [$label]: " + el)
    println(s"get [$label] 2: " + res)
    res
  } { newValue => elem =>
    // TODO: rethink iterator usage
    val newValues = newValue.toIterator
    val newChild = elem.child.map {
      case currentEl: Elem if currentEl.label == label =>
        newValues.next()
      case el => el
    }
    println(s"set [$label]: " + newValue)
    elem.copy(child = newChild)
  }
}

object XmlOptics extends XmlOptics {

}
