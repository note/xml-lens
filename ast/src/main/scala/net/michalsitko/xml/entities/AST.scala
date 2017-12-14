package net.michalsitko.xml.entities

// content according to https://www.w3.org/TR/xml/#NT-content
sealed trait Node

// as defined here https://www.w3.org/TR/xml/#NT-Misc it is:
// Misc	   ::=   	Comment | PI | S
// In this implementation have only two subtypes of Misc (Comment and processing instruction)
// that means that we do not preserve whitespaces at top level of XML document
sealed trait Misc

/** First - why we need different entities than plain scala-xml ones?
  *
  * The problem appeared when testing lawfulness of Optional[Elem, NonEmptyList[Elem]]
  * It was impossible to satisfy `get what you set` law as `modify` function may
  * modify `label` which at the same time is the key for a lookup.
  *
  * // just pseudo-code - elem is not a case class so it has no `copy`
  * val res = elemOptional.get("abc").modify(_.copy(label = "someNewLabel"))
  * res.get("abc") // will return None instead of modified elem...
  *
  * To restrict user not to modify label of "zoomed-in" element we need to create our own Element
  *
  */
case class LabeledElement(label: ResolvedName, element: Element) extends Node

object LabeledElement {
  def unprefixed(localName: String, element: Element): LabeledElement =
    LabeledElement(ResolvedName.unprefixed(localName), element)

  def unprefixedEmpty(localName: String): LabeledElement =
    LabeledElement.unprefixed(localName, Element())
}

// CharData according to https://www.w3.org/TR/xml/#NT-CharData
case class Text(text: String) extends Node

// Processing instruction according to https://www.w3.org/TR/xml/#NT-PI
case class ProcessingInstruction(target: String, data: String) extends Node with Misc

// TDSect according to https://www.w3.org/TR/xml/#NT-CDSect
case class CData(text: String) extends Node

case class EntityReference(name: String, replacement: String) extends Node

// Comment according to https://www.w3.org/TR/xml/#NT-Comment
case class Comment(comment: String) extends Node with Misc

// TODO: think if Seq[Attribute] is a good choice taking into account that attribute names have to be unique within
// single element and printing with XmlStreamWriter a non-unique Attribute will throw an exception
case class Element(attributes: Seq[Attribute] = Seq.empty, children: Seq[Node] = Seq.empty, namespaceDeclarations: Seq[NamespaceDeclaration] = Seq.empty)

// when no prefix in XML then: prefix == ""
// take a look at: https://www.w3.org/TR/xml/#NT-AttValue
case class Attribute(key: ResolvedName, value: String) {
  // TODO: should it stay here?
  def sameKey(anotherKey: ResolvedName): Boolean = {
    (key.prefix == anotherKey.prefix) && (key.localName == anotherKey.localName)
  }
}

object Attribute {
  def unprefixed(key: String, value: String): Attribute =
    Attribute(ResolvedName.unprefixed(key), value)
}

// should prefix and/or uri be optional?
case class ResolvedName(prefix: String, uri: String, localName: String) {
  def hasPrefix: Boolean = prefix.nonEmpty
}

object ResolvedName {
  def unprefixed(localName: String): ResolvedName =
    ResolvedName("", "", localName)
}

// https://www.w3.org/TR/xml-names/#ns-decl
// `prefix` is empty for default namespace
case class NamespaceDeclaration(prefix: String, uri: String)

// https://www.w3.org/TR/xml/#NT-prolog
case class Prolog(xmlDeclaration: Option[XmlDeclaration], miscs: Seq[Misc], doctypeDeclaration: Option[(DoctypeDeclaration, Seq[Misc])])

// https://www.w3.org/TR/xml/#NT-XMLDecl
case class XmlDeclaration(version: String, encoding: Option[String])

// Document type declaration according to https://www.w3.org/TR/xml/#NT-doctypedecl
case class DoctypeDeclaration(text: String)

case class XmlDocument(prolog: Prolog, root: LabeledElement)
