package net.michalsitko.xml.syntax

import monocle._
import net.michalsitko.xml.entities.{Attribute, Element, LabeledElement, NamespaceDeclaration}
import net.michalsitko.xml.optics.ElementOptics.allLabeledElements
import net.michalsitko.xml.optics._

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
    current.composeOptional(ElementOptics.attribute(nameMatcher))
  )

  def attrs: AttributesBuilder = AttributesBuilder (
    current.composeLens(ElementOptics.attributes)
  )

  def replaceOrAddAttr(key: NameMatcher with ToResolvedName, newValue: String): LabeledElement => LabeledElement = { el =>
    val modifyExisting = ElementOptics.attribute(key).modifyOption(_ => newValue)

    val addNs: (Element) => Element = key match {
      case matcher: PrefixedResolvedNameMatcher if matcher.uri.nonEmpty =>
        ElementOptics.namespaces.modify(ns => ns :+ NamespaceDeclaration(matcher.prefix, matcher.uri))
      case _ =>
        identity[Element]_
    }

    current.modify { elem =>
      modifyExisting(elem) match {
        case Some(changedElem) =>
          changedElem
        case None =>
          val changed = ElementOptics.attributes.modify(attrs => attrs :+ Attribute(key.toResolvedName, newValue))(elem)
          addNs(changed)
      }
    }(el)
  }

  def renameLabel(oldLabel: NameMatcher with ToResolvedName, newLabel: NameMatcher with ToResolvedName): LabeledElement => LabeledElement = { labeledElement =>
    current.modify { element =>
      allLabeledElements.modify { el =>
        if (oldLabel.matches(el.label)) {
          LabeledElementOptics.label.set(newLabel.toResolvedName)(el)
        } else {
          el
        }
      }(element)
    }(labeledElement)
  }

  def renameLabel(oldLabel: String, newLabel: String): LabeledElement => LabeledElement =
    renameLabel(NameMatcher.fromString(oldLabel), NameMatcher.fromString(newLabel))

  def replaceOrAddAttr(key: String, newValue: String): LabeledElement => LabeledElement =
    replaceOrAddAttr(NameMatcher.fromString(key), newValue)

  def hasTextOnly: TextBuilder = TextBuilder (
    current.composeOptional(ElementOptics.hasTextOnly)
  )
}