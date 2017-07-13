package net.michalsitko.xml.syntax

import monocle._
import net.michalsitko.xml.entities._
import net.michalsitko.xml.optics._

import scalaz.Applicative

object OpticsBuilder {
  def root = new RootBuilder
}

class RootBuilder extends AnyRef with ElementOps {
  val current = Lens[LabeledElement, Element](_.element)(newElement => from => from.copy(element = newElement)).asTraversal

  def \ (nameMatcher: String): DeepBuilder = {
    \ (NameMatcher.fromString(nameMatcher))
  }

  def \ (nameMatcher: NameMatcher): DeepBuilder = DeepBuilder (
    LabeledElementOptics.deep(nameMatcher)
  )

}

case class DeepBuilder(current: Traversal[LabeledElement, Element]) extends AnyRef with ElementOps {
  def \ (nameMatcher: String): DeepBuilder = {
    \ (NameMatcher.fromString(nameMatcher))
  }

  def \ (nameMatcher: NameMatcher): DeepBuilder = DeepBuilder (
    current.composeTraversal(ElementOptics.deeper(nameMatcher))
  )

  def having(predicate: Node => Boolean): DeepBuilder = {
    // TODO: extract it to optics
    val traversal = new Traversal[Element, Element] {
      override final def modifyF[F[_]: Applicative](f: (Element) => F[Element])(from: Element): F[Element] = {
        if(from.children.exists(predicate)) {
          f(from)
        } else {
          Applicative[F].pure(from)
        }
      }
    }

    val composed: Traversal[LabeledElement, Element] = current.composeTraversal(traversal)
    DeepBuilder(composed)
  }

  def index(idx: Int): DeepBuilderOptional = {
    val optional: Optional[LabeledElement, Element] = Optional.apply[LabeledElement, Element] { root =>
      val all = current.getAll(root)
      all.lift(idx)
    }{ updatedElem => root =>
      current.modify(new Indexed(idx, updatedElem))(root)
    }
    DeepBuilderOptional(optional)
  }

  def childAt(idx: Int): DeepBuilder = {
    val index = ElementOptics.index
    val optional: Optional[Element, Node] = index.index(idx)
    val newTraversal = current.composeOptional(optional).composePrism(NodeOptics.isLabeledElement).composeLens(LabeledElementOptics.element)
    DeepBuilder(newTraversal)
  }

  def elementAt(idx: Int): DeepBuilder = {
    val optional = ElementOptics.indexElementOptional(idx)
    val newTraversal = {
      current.composeOptional(optional).composeLens(LabeledElementOptics.element)
    }
    DeepBuilder(newTraversal)
  }
}


class IndexedPredicateUpdater[T](idx: Int, updatePF: PartialFunction[T, T]) extends (T => T) {
  private var counter = 0

  override def apply(v1: T): T = {
    if(updatePF.isDefinedAt(v1)) {
      val res = if(counter == idx) {
        updatePF.apply(v1)
      } else {
        v1
      }
      counter += 1
      res
    } else {
      v1
    }
  }
}


// TODO: make it private
class Indexed(idx: Int, newValue: Element) extends (Element => Element) {
  private var counter = 0

  override def apply(v1: Element): Element = {
    val r = if (counter == idx) {
      newValue
    } else {
      v1
    }
    counter += 1
    r
  }
}


object DeepBuilder {
  implicit def toTraversal(builder: DeepBuilder): Traversal[LabeledElement, Element] =
    builder.current
}

case class DeepBuilderOptional(currentOptional: Optional[LabeledElement, Element]) extends AnyRef with ElementOps {
  override val current = currentOptional.asTraversal

  def \ (nameMatcher: String): DeepBuilder = {
    \ (NameMatcher.fromString(nameMatcher))
  }

  def \ (nameMatcher: NameMatcher): DeepBuilder = DeepBuilder (
    current.composeTraversal(ElementOptics.deeper(nameMatcher))
  )

  // DeepBuilderOptional intentionally does not have `having` method
  // It would be useful only for such constructs:
  // (root \ "some").having(predicate1).having(predicate2)
  // Namely, only while one `having` follows directly another `having`
  // It's not needed as we can express abovementioned snippet with:
  // (root \ "some").having(n => predicate1(n) && predicate2(n))
}

object DeepBuilderOptional {
  implicit def toOptional(builder: DeepBuilderOptional): Optional[LabeledElement, Element] =
    builder.currentOptional
}

case class TextBuilder(current: Traversal[LabeledElement, String])

object TextBuilder {
  implicit def toTraversal(builder: TextBuilder): Traversal[LabeledElement, String] =
    builder.current
}

case class AttributesBuilder(current: Traversal[LabeledElement, Seq[Attribute]])

object AttributesBuilder {
  implicit def toTraversal(builder: AttributesBuilder): Traversal[LabeledElement, Seq[Attribute]] =
    builder.current
}
