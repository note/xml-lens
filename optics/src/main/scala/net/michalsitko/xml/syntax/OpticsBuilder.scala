package net.michalsitko.xml.syntax

import monocle.{Iso, PTraversal, Traversal}
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

case class DeepBuilder(current: Traversal[LabeledElement, Element]) extends OpticsBuilder {
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
