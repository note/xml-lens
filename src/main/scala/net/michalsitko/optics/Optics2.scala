package net.michalsitko.optics

import monocle.{Lens, Prism, Traversal}

import scala.collection.immutable.Seq
import scala.xml.{Elem, Node, NodeSeq}
import scalaz.Applicative
import scalaz.std.list._


trait Optics2 {
  def nodeLens(fieldName: String): Lens[Node, NodeSeq] = Lens.apply[Node, NodeSeq](
    elem => elem \ fieldName
  ){newNodeSeq => rootNode =>
    println("bazinga 200: " + newNodeSeq)
    val it = newNodeSeq.toIterator
    val children = rootNode.child.flatMap {
      case el: Elem if el.label == fieldName =>
        if(it.hasNext){
          val r = it.next()
          println(s"bazinga in lens1 [$fieldName]: " + r)
          Some(r)
        } else {
          None
        }
      case el =>
        println(s"bazinga in lens1 [$fieldName] miss: " + el)
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
        val children = e.child.map {
          case el: Elem if el.label == fieldName =>
            val r = it.next()
            println(s"bazinga in lens2[$fieldName]: " + r)
            r
          case el =>
            println(s"bazinga in lens2 [$fieldName] miss: " + el)
            el
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

//  def elementLens(fieldName: String): Lens[Elem, NodeSeq] = Lens.apply[Elem, NodeSeq](
//    elem => elem \ fieldName
//  )(newNodeSeq => rootElem => rootElem.map {
//    case el: Elem if el.label == fieldName =>
//      el
//    case a => a
//  }.head)

  // we don't know how to implement set - thus it cannot be modelled as Lens
//  def nodeSeqLens(fieldName: String): Lens[NodeSeq, NodeSeq] = Lens.apply[NodeSeq, NodeSeq](
//    nodeSeq => nodeSeq \ fieldName
//  )(nodeSeq => root => root.map {
//    case el: Elem if el.label == fieldName =>
//      el
//  })



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

  val listIntTraversal = new Traversal[List[Int], Int]{
    final def modifyF[F[_]](f: Int => F[Int])(from: List[Int])(implicit F: Applicative[F]): F[List[Int]] = {
      val mapped: List[F[Int]] = from.map(f)
      F.sequence(mapped)
    }
  }

  def nodeSeqTraversal(nodeName: String) = new Traversal[NodeSeq, Node]{
    final def modifyF[F[_]](f: Node => F[Node])(from: NodeSeq)(implicit
                                                                  F: Applicative[F]
    ): F[NodeSeq] = {
      val elements = from.theSeq.collect {
        case el: Elem => el
      }.toList
      val a: Seq[F[Node]] = elements.collect {
        case elem: Elem if elem.label == nodeName =>
          println("bazinga t1: " + elem.label)
          elem
//        case elem =>
//          F.pure(elem)
      }.flatMap {
        case e: Elem =>
          println("bazinga 101: " + e.child)
          e.child
      }.map(f)

      // probably we should use transform API (performance)
      F.map(F.sequence(a.toList))(nodes => NodeSeq.fromSeq(nodes))


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

      val tmp: List[F[Node]] = from.map {
        case elem: Elem if elem.label == nodeName =>
          println("bazinga t2: " + elem.label)
          f(elem)
        case elem =>
          println("bazinga t3 0: " + nodeName)
          println("bazinga t3 1: " + elem)
          F.pure(elem)
      }.toList
      println("bazinga tmp: " + tmp)

      // this `.head` is non obvious
      val res = F.map(F.sequence(tmp))(nodes => nodes.head)
      println("bazinga res: " + res)
      res
    }

  }

}

object Optics2 extends Optics2
