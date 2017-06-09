package net.michalsitko.xml.optics

import monocle.{Optional, Traversal}
import net.michalsitko.xml.entities._

import scalaz.Applicative

object Optics {
  import scalaz.std.list._
  import scalaz.syntax.traverse._
  import scalaz.syntax.applicative._


  // TODO: test laws (20 min)
  // TODO: implement other lenses and check if most popular use cases case covered (3 h)
  def deep(label: ResolvedName): Traversal[LabeledElement, Element] = new Traversal[LabeledElement, Element] {
    override final def modifyF[F[_]](f: (Element) => F[Element])(from: LabeledElement)(implicit F: Applicative[F]): F[LabeledElement] = {
      val tmp = from.element.children.collect {
        // TODO: equals for ResolvedName should return true for different prefixes but same fields otherwise
        case el: LabeledElement if el.label == label =>
          val mapped = f(el.element)
          F.map(mapped)(mappedDetail => LabeledElement(label, mappedDetail).asInstanceOf[Node])
        case anythingElse: Node =>
          F.pure(anythingElse)
      }.toList

      F.map(F.sequence(tmp)){ elements =>
        from.copy(element = from.element.copy(children = elements))
      }
    }
  }

  def deeper(label: ResolvedName): Traversal[Element, Element] = new Traversal[Element, Element] {
    override final def modifyF[F[_]](f: (Element) => F[Element])(from: Element)(implicit F: Applicative[F]): F[Element] = {
      val tmp = from.children.collect {
        // TODO: equals for ResolvedName should return true for different prefixes but same fields otherwise
        case el: LabeledElement if el.label == label =>
          val mapped = f(el.element)
          F.map(mapped)(mappedDetail => LabeledElement(label, mappedDetail).asInstanceOf[Node])
        case anythingElse =>
          F.pure(anythingElse)
      }.toList

      F.map(F.sequence(tmp)){ elements =>
        from.copy(children = elements)
      }
    }
  }

}
