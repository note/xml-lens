package net.michalsitko.optics

import monocle.{Lens, Traversal}

import scala.xml.{Elem, Node, NodeSeq}
import scalaz.{Applicative, Traverse}
import scala.collection.immutable.Seq
import scalaz.std.list._
import scalaz.std.iterable._


trait Optics2 {
  // we don't know how to implement set - thus it cannot be modelled as Lens
  def nodeSeqLens(fieldName: String): Lens[NodeSeq, NodeSeq] = Lens.apply[NodeSeq, NodeSeq](
    nodeSeq => nodeSeq \ fieldName
  )(nodeSeq => root => root.map {
    case el: Elem if el.label == fieldName =>
      el
  })

//  implicit def nodeSeqTraverse(nodeSeq: NodeSeqT[Node]): Traverse[NodeSeqT] = {
//    new Traverse[NodeSeqT]{
//      override def traverseImpl[F[_], A, B](l: NodeSeqT[A])(f: (A) => F[B])(implicit F: Applicative[F]): F[NodeSeqT[B]] = {
//        l.reverse.foldLeft(F.point(NodeSeq.Empty: NodeSeqT[B])) { (flb: F[NodeSeqT[B]], a: Node) =>
//          F.apply2[Node, Node, NodeSeq](f(a), flb){(a, b) => NodeSeq.fromSeq(a.asInstanceOf[Node] +: b.toSeq)}
//        }
//      }
//    }
////    scalaz.std.list.listInstance.traverseImpl[List[_], Node, B](asList)(f)
//  }


  def nodeSeqTraversal(nodeName: String) = new Traversal[NodeSeq, Node]{
    final def modifyF[F[_]](f: Node => F[Node])(from: NodeSeq)(implicit
                                                                  F: Applicative[F]
    ): F[NodeSeq] = {
      val a: List[F[Node]] = from.map {
        case elem: Elem if elem.label == nodeName =>
          println("bazinga t1: " + elem.label)
          f(elem)
        case elem =>
          F.pure(elem)
      }.toList

      // probably we should use transform API (performance)
      F.map(F.sequence(a))(nodes => NodeSeq.fromSeq(nodes))


      // TODO: such semantics is probably good just for each or all?
//      val r = (from \ nodeName).foldLeft(F.pure(List.empty[Node])) { (acc, curr) =>
//        F.apply2(acc, f(curr))((acc, fcurr) => fcurr :: acc)
//      }
//      F.map(r)(nodes => NodeSeq.seqToNodeSeq(nodes))
    }

  }

  def nodeSeqTraversal2(nodeName: String) = new Traversal[Node, Node]{
    final def modifyF[F[_]](f: Node => F[Node])(from: Node)(implicit
                                                               F: Applicative[F]
    ): F[Node] = {

      val a: List[F[Node]] = from.map {
        case elem: Elem if elem.label == nodeName =>
          println("bazinga t2: " + elem.label)
          f(elem)
        case elem =>
          println("bazinga t3: " + elem)
          F.pure(elem)
      }.toList

      // this `.head` is non obvious
      F.map(F.sequence(a))(nodes => nodes.head)
    }

  }

}

object Optics2 extends Optics2
