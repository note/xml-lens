package net.michalsitko.xml.syntax

import monocle.{Iso, Traversal}
import net.michalsitko.xml.entities.{Attribute, Element, LabeledElement}
import net.michalsitko.xml.optics.{NameMatcher, Optics}

// TODO: Is it needed at all?
trait OpticsBuilder {
}

object OpticsBuilder {
  def root = new RootBuilder
}

class RootBuilder extends OpticsBuilder {
  val current = Iso.apply[LabeledElement, LabeledElement](identity)(identity)

  def \ (nameMatcher: String): DeepBuilder = {
    \ (NameMatcher.fromString(nameMatcher))
  }

  def \ (nameMatcher: NameMatcher): DeepBuilder = {
    val r: Traversal[LabeledElement, Element] = Optics.deep(nameMatcher)
    DeepBuilder(r)
  }
}

case class DeepBuilder(current: Traversal[LabeledElement, Element]) extends OpticsBuilder with ElementOps {
  def \ (nameMatcher: String): DeepBuilder = {
    \ (NameMatcher.fromString(nameMatcher))
  }

  def \ (nameMatcher: NameMatcher): DeepBuilder = {
    val r = current.composeTraversal(Optics.deeper(nameMatcher))
    DeepBuilder(r)
  }

  def hasTextOnly: TextBuilder = {
    val r: Traversal[LabeledElement, String] = current.composeOptional(Optics.hasTextOnly)
    TextBuilder(r)
  }

  def attr (nameMatcher: String): TextBuilder =
    attr(NameMatcher.fromString(nameMatcher))

  def attr (nameMatcher: NameMatcher): TextBuilder = {
    val r: Traversal[LabeledElement, String] = current.composeOptional(Optics.attribute(nameMatcher))
    TextBuilder(r)
  }

  def attrs: AttributesBuilder = {
    val r = current.composeLens(Optics.attributes)
    AttributesBuilder(r)
  }

  override def replaceOrAddAttr(key: NameMatcher, newValue: String): (LabeledElement) => LabeledElement = {
    // TODO: change this implementation!
    val replaceIfExistsTraversal = current.composeOptional((Optics.attribute(key)))
    val replaceForExisting = replaceIfExistsTraversal.modify(_ => newValue)
    val addOtherwiseTraversal = current.composeLens(Optics.attributes)
    val addNonExisting = addOtherwiseTraversal.modify { attrs =>
      if(attrs.exists(attr => key.matches(attr.key))) {
        attrs
      } else {
        // TODO: may need to be changed to (e.g. with some extra effort we can try to avoid unreferenced namespaces)
        attrs :+ Attribute(key.toResolvedName, newValue)
      }
    }
    replaceForExisting andThen addNonExisting
  }
}

object DeepBuilder {
  implicit def toTraversal(builder: DeepBuilder): Traversal[LabeledElement, Element] =
    builder.current
}

case class TextBuilder(current: Traversal[LabeledElement, String]) extends OpticsBuilder

object TextBuilder {
  implicit def toTraversal(builder: TextBuilder): Traversal[LabeledElement, String] =
    builder.current
}

case class AttributesBuilder(current: Traversal[LabeledElement, Seq[Attribute]]) extends OpticsBuilder

object AttributesBuilder {
  implicit def toTraversal(builder: AttributesBuilder): Traversal[LabeledElement, Seq[Attribute]] =
    builder.current
}

trait ElementOps {
  def replaceOrAddAttr(key: NameMatcher, newValue: String): LabeledElement => LabeledElement
}