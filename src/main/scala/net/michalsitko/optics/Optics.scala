package net.michalsitko.optics

import cats.data.NonEmptyList
import monocle.{Lens, Optional, Prism, Traversal}

import scala.xml.{Elem, Node, NodeSeq}
import scalaz.Applicative
import scalaz.std.list._


trait Optics {
//  def elemOptional(fieldName: String): Optional[Node, NodeSeq] = Optional[Node, NodeSeq](
//
//  )

  def elem(label: String): Optional[Elem, NonEmptyList[Elem]] = Optional[Elem, NonEmptyList[Elem]]{
    parent =>
      val nodes = parent \ label
      val elems = nodes.foldRight(List.empty[Elem]) { (current, acc) =>
        current match {
          case elem: Elem => elem :: acc
        }
      }
      NonEmptyList.fromList(elems)
  }{ newElems => parent =>
    val it = newElems.toList.toIterator
    val children = parent.child.flatMap {
      case el: Elem if el.label == label =>
        if(it.hasNext){
          Some(it.next())
        } else {
          None
        }
      case el =>
        Some(el)
    }
    parent.copy(child = children)
  }

  def nodeLens(fieldName: String): Lens[Node, NodeSeq] = Lens.apply[Node, NodeSeq](
    elem => elem \ fieldName
  ){newNodeSeq => rootNode =>
    val it = newNodeSeq.toIterator
    val children = rootNode.child.flatMap {
      case el: Elem if el.label == fieldName =>
        if(it.hasNext){
          Some(it.next())
        } else {
          None
        }
      case el =>
        Some(el)
    }
    rootNode.asInstanceOf[Elem].copy(child = children)
  }

  def nodeLens2(fieldName: String): Lens[NodeSeq, NodeSeq] = Lens.apply[NodeSeq, NodeSeq](
    elem => elem \ fieldName
  ){newNodeSeq => rootNode =>
    val it = newNodeSeq.toIterator
    val r = rootNode.map {
      case e: Elem =>
        val children = e.child.flatMap {
          case el: Elem if el.label == fieldName =>
            if(it.hasNext){
              Some(it.next)
            } else {
              None
            }
          case el =>
            Some(el)
        }
        e.copy(child = children)
      case e => e
    }
    NodeSeq.fromSeq(r)
  }

  val elemPrism: Prism[Node, Elem] = Prism[Node, Elem](node => node match {
    case e: Elem => Some(e)
    case _ => None
  })(el => el)

  val each = new Traversal[NodeSeq, Node]{
    final def modifyF[F[_]](f: Node => F[Node])(from: NodeSeq)(implicit F: Applicative[F]): F[NodeSeq] = {
      val mapped = from.theSeq.map(f).toList
      F.map(F.sequence(mapped))(nodes => NodeSeq.fromSeq(nodes))
    }
  }

}

object Optics extends Optics
