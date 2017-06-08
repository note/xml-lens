package net.michalsitko.xml.optics

import monocle.{Optional, Traversal}
import net.michalsitko.xml.entities._

import scalaz.Applicative

object Optics {
  import scalaz.std.list._
  import scalaz.syntax.traverse._
  import scalaz.syntax.applicative._


  // TODO: test manually if behaves as expected (20 min)
  // TODO: test laws (20 min)
  // TODO: implement other lenses and check if most popular use cases case covered (3 h)
  def deeper(label: ResolvedName): Traversal[Element, Details] = new Traversal[Element, Details] {
    override final def modifyF[F[_]](f: (Details) => F[Details])(from: Element)(implicit F: Applicative[F]): F[Element] = {
      val tmp = from.elementDetails.children.collect {
        // TODO: equals for ResolvedName should return true for different prefixes but same fields otherwise
        case el: Element if el.label == label =>
          val mapped = f(el.elementDetails)
          F.map(mapped)(mappedDetail => Element(label, mappedDetail).asInstanceOf[Node])
        case anythingElse: Node =>
          F.pure(anythingElse)
      }.toList

      F.map(F.sequence(tmp)){ details =>
        from.copy(elementDetails = from.elementDetails.copy(children = details))
      }
    }
  }

  def deeperDetails(label: ResolvedName): Traversal[Details, Details] = new Traversal[Details, Details] {
    override final def modifyF[F[_]](f: (Details) => F[Details])(from: Details)(implicit F: Applicative[F]): F[Details] = {
      val tmp = from.children.collect {
        // TODO: equals for ResolvedName should return true for different prefixes but same fields otherwise
        case el: Element if el.label == label =>
          val mapped = f(el.elementDetails)
          F.map(mapped)(mappedDetail => Element(label, mappedDetail).asInstanceOf[Node])
        case anythingElse =>
          F.pure(anythingElse)
      }.toList

      F.map(F.sequence(tmp)){ details =>
        from.copy(children = details)
      }
    }
  }

}
