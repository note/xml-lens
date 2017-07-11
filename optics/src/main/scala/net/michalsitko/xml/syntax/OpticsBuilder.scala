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
    val optional: Optional[LabeledElement, Element] = Optional.apply[LabeledElement, Element] { labeled =>
      val all = current.getAll(labeled)
      all.lift(idx)
    }{ newElem => labeled =>
      current.modify(new Indexed(idx, newElem))(labeled)
    }
    DeepBuilderOptional(optional)
  }

  def childAt(idx: Int): DeepBuilder = {
    val index = ElementOptics.index
    val optional = index.index(idx)
    val newTraversal = current.composeOptional(optional).composePrism(NodeOptics.isLabeledElement).composeLens(LabeledElementOptics.element)
    DeepBuilder(newTraversal)
  }
}

private [syntax] class Indexed(idx: Int, newValue: Element) extends (Element => Element) {
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

  def having(predicate: Node => Boolean): DeepBuilderOptional = {
    val optional = Optional.apply[Element, Element] { element =>
      if (element.children.exists(predicate)) {
        Some(element)
      } else {
        None
      }
    } { newElement => element =>
      if (element.children.exists(predicate)) {
        newElement
      } else {
        element
      }
    }

    val composed = currentOptional.composeOptional(optional)
    DeepBuilderOptional(composed)
  }
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
