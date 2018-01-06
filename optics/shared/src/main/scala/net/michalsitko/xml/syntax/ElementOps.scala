package net.michalsitko.xml.syntax

import monocle.Traversal
import net.michalsitko.xml.entities._
import net.michalsitko.xml.optics.ElementOptics.allLabeledElements
import net.michalsitko.xml.optics._

trait ElementOps {
  def current: Traversal[XmlDocument, Element]

  def attr(nameMatcher: String): TextBuilder =
    attr(NameMatcher.fromString(nameMatcher))

  def attr(nameMatcher: NameMatcher): TextBuilder = TextBuilder (
    current.composeOptional(ElementOptics.attribute(nameMatcher))
  )

  def attrs: AttributesBuilder = AttributesBuilder (
    current.composeLens(ElementOptics.attributes)
  )

  def replaceOrAddAttr(key: NameMatcher with ToResolvedName, newValue: String): XmlDocument => XmlDocument = { el =>
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

  def renameLabel(oldLabel: NameMatcher with ToResolvedName, newLabel: NameMatcher with ToResolvedName): XmlDocument => XmlDocument = { labeledElement =>
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

  def renameLabel(oldLabel: String, newLabel: String): XmlDocument => XmlDocument =
    renameLabel(NameMatcher.fromString(oldLabel), NameMatcher.fromString(newLabel))

  def replaceOrAddAttr(key: String, newValue: String): XmlDocument => XmlDocument =
    replaceOrAddAttr(NameMatcher.fromString(key), newValue)

  def hasTextOnly: TextBuilder = TextBuilder (
    current.composeOptional(ElementOptics.hasTextOnly)
  )
}
