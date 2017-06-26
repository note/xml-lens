package net.michalsitko.xml.syntax

import monocle._
import net.michalsitko.xml.entities.{Attribute, Element, LabeledElement, NamespaceDeclaration}
import net.michalsitko.xml.optics.{NameMatcher, Optics, ResolvedNameMatcher}

object OpticsBuilder {
  def root = new RootBuilder
}

class RootBuilder extends AnyRef with ElementOps {
  val current = Lens[LabeledElement, Element](_.element)(newElement => from => from.copy(element = newElement)).asTraversal

  def \ (nameMatcher: String): DeepBuilder = {
    \ (NameMatcher.fromString(nameMatcher))
  }

  def \ (nameMatcher: NameMatcher): DeepBuilder = DeepBuilder (
    Optics.deep(nameMatcher)
  )

}

case class DeepBuilder(current: Traversal[LabeledElement, Element]) extends AnyRef with ElementOps {
  def \ (nameMatcher: String): DeepBuilder = {
    \ (NameMatcher.fromString(nameMatcher))
  }

  def \ (nameMatcher: NameMatcher): DeepBuilder = DeepBuilder (
    current.composeTraversal(Optics.deeper(nameMatcher))
  )
}

object DeepBuilder {
  implicit def toTraversal(builder: DeepBuilder): Traversal[LabeledElement, Element] =
    builder.current
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

trait ElementOps {
  def current: Traversal[LabeledElement, Element]

  def attr(nameMatcher: String): TextBuilder =
    attr(NameMatcher.fromString(nameMatcher))

  def attr(nameMatcher: NameMatcher): TextBuilder = TextBuilder (
    current.composeOptional(Optics.attribute(nameMatcher))
  )

  def attrs: AttributesBuilder = AttributesBuilder (
    current.composeLens(Optics.attributes)
  )

  def replaceOrAddAttr(key: NameMatcher, newValue: String): (LabeledElement) => LabeledElement = { el =>
    val modifyExisting = Optics.attribute(key).modifyOption(_ => newValue)

    val addNs: (Element) => Element = key match {
      case matcher: ResolvedNameMatcher if matcher.uri.isDefined =>
        Optics.namespaces.modify(ns => ns :+ NamespaceDeclaration(Some(matcher.prefix), matcher.uri.get))
      case _ =>
        identity[Element]_
    }

    current.modify { elem =>
      modifyExisting(elem) match {
        case Some(changedElem) =>
          changedElem
        case None =>
          val changed = Optics.attributes.modify(attrs => attrs :+ Attribute(key.toResolvedName, newValue))(elem)
          addNs(changed)
      }
    }(el)
  }

  def replaceOrAddAttr(key: String, newValue: String): (LabeledElement) => LabeledElement =
    replaceOrAddAttr(NameMatcher.fromString(key), newValue)

  def hasTextOnly: TextBuilder = TextBuilder (
    current.composeOptional(Optics.hasTextOnly)
  )
}