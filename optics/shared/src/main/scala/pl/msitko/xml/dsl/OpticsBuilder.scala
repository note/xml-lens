package pl.msitko.xml.dsl

import monocle._
import pl.msitko.xml.entities._
import pl.msitko.xml.matchers.NameMatcher
import pl.msitko.xml.optics._

import scalaz.Applicative

object OpticsBuilder {
  def root = new RootBuilder
}

class RootBuilder extends AnyRef with ElementOps {
  import pl.msitko.xml.optics.XmlDocumentOptics._
  import pl.msitko.xml.optics.LabeledElementOptics._

  val current = rootLens.composeLens(element).asTraversal

  def \ (nameMatcher: String): DeepBuilder = {
    \ (NameMatcher.fromString(nameMatcher))
  }

  def \ (nameMatcher: NameMatcher): DeepBuilder = DeepBuilder (
    rootLens.composeTraversal(LabeledElementOptics.deep(nameMatcher))
  )

}

final case class DeepBuilder(current: Traversal[XmlDocument, Element]) extends AnyRef with ElementOps {
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

    val composed: Traversal[XmlDocument, Element] = current.composeTraversal(traversal)
    DeepBuilder(composed)
  }

  def index(n: Int): DeepBuilderOptional = {
    val optional: Optional[XmlDocument, Element] = Optional.apply[XmlDocument, Element] { root =>
      val all = current.getAll(root)
      all.lift(n)
    }{ updatedElem => root =>
      current.modify(new Indexed(n, updatedElem))(root)
    }
    DeepBuilderOptional(optional)
  }

  def childAt(n: Int): DeepBuilder = {
    val index = ElementOptics.index
    val optional: Optional[Element, Node] = index.index(n)
    val newTraversal = current
      .composeOptional(optional)
      .composePrism(NodeOptics.isLabeledElement)
      .composeLens(LabeledElementOptics.element)
    DeepBuilder(newTraversal)
  }

  val children = current.composeLens(ElementOptics.children)

  def elementAt(n: Int): DeepBuilder = {
    val optional = ElementOptics.indexElementOptional(n)
    val newTraversal = {
      current.composeOptional(optional).composeLens(LabeledElementOptics.element)
    }
    DeepBuilder(newTraversal)
  }
}

private [dsl] class Indexed(idx: Int, newValue: Element) extends (Element => Element) {
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
  implicit def toTraversal(builder: DeepBuilder): Traversal[XmlDocument, Element] =
    builder.current
}

final case class DeepBuilderOptional(currentOptional: Optional[XmlDocument, Element]) extends AnyRef with ElementOps {
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
  // It's not needed as we can express above mentioned snippet with:
  // (root \ "some").having(n => predicate1(n) && predicate2(n))
}

object DeepBuilderOptional {
  implicit def toOptional(builder: DeepBuilderOptional): Optional[XmlDocument, Element] =
    builder.currentOptional
}

final case class TextBuilder(current: Traversal[XmlDocument, String])

object TextBuilder {
  implicit def toTraversal(builder: TextBuilder): Traversal[XmlDocument, String] =
    builder.current
}

final case class AttributesBuilder(current: Traversal[XmlDocument, Seq[Attribute]])

object AttributesBuilder {
  implicit def toTraversal(builder: AttributesBuilder): Traversal[XmlDocument, Seq[Attribute]] =
    builder.current
}
