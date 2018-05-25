package pl.msitko.xml.entities

/**
  * As documented here: https://www.w3.org/TR/xml/#NT-content
  */
sealed trait Node

/**
  * According to https://www.w3.org/TR/xml/#NT-Misc it is:
  * Misc	   ::=   	Comment | PI | S
  *
  * For sake of simplicity xml-lens defines it as:
  * Misc	   ::=   	Comment | PI
  *
  * That means that we do not preserve whitespaces outside of root element
  */
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
final case class LabeledElement(label: ResolvedName, element: Element) extends Node

object LabeledElement {
  def unprefixed(localName: String, element: Element): LabeledElement =
    LabeledElement(ResolvedName.unprefixed(localName), element)

  def unprefixed(localName: String): LabeledElement =
    LabeledElement.unprefixed(localName, Element())
}

/**
  * As documented here: https://www.w3.org/TR/xml/#NT-CharData
  */
final case class Text(text: String) extends Node

/**
  * As documented here: https://www.w3.org/TR/xml/#NT-PI
  */
final case class ProcessingInstruction(target: String, data: String) extends Node with Misc

/**
  * As documented here: https://www.w3.org/TR/xml/#NT-CDSect
  */
final case class CData(text: String) extends Node

final case class EntityReference(name: String, replacement: String) extends Node

/**
  * As documented here: https://www.w3.org/TR/xml/#NT-Comment
  */
final case class Comment(comment: String) extends Node with Misc

final case class Element(attributes: Seq[Attribute] = Seq.empty, children: Seq[Node] = Seq.empty, namespaceDeclarations: Seq[NamespaceDeclaration] = Seq.empty)

final case class Attribute(key: ResolvedName, value: String)

object Attribute {
  def unprefixed(key: String, value: String): Attribute =
    Attribute(ResolvedName.unprefixed(key), value)
}

/**
  * empty prefix is encoded as "" (empty string)
  * empty uri is encoded as "" (empty string)
  *
  * Such encoding was used instead of Option[String] because with Option encoding there will be 2 encodings
  * (i.e. None and "") for the same situations. I was also a bit scared of performance penalty but haven't
  * really checked that
  */
final case class ResolvedName(prefix: String, uri: String, localName: String) {
  def hasPrefix: Boolean = prefix.nonEmpty
}

object ResolvedName {
  def unprefixed(localName: String): ResolvedName =
    ResolvedName("", "", localName)
}

// https://www.w3.org/TR/xml-names/#ns-decl
// `prefix` is empty for default namespace
final case class NamespaceDeclaration(prefix: String, uri: String) {
  def resolvedName(localName: String): ResolvedName = ResolvedName(prefix, uri, localName)
}

/**
  * As documented here: https://www.w3.org/TR/xml/#NT-prolog
  */
final case class Prolog(xmlDeclaration: Option[XmlDeclaration], miscs: Seq[Misc], doctypeDeclaration: Option[(DoctypeDeclaration, Seq[Misc])])

/**
  * As documented here: https://www.w3.org/TR/xml/#NT-XMLDecl
  */
final case class XmlDeclaration(version: String, encoding: Option[String])

/**
  * As documented here: https://www.w3.org/TR/xml/#NT-doctypedecl
  */
final case class DoctypeDeclaration(text: String)

/**
  * According to https://www.w3.org/TR/xml/#sec-well-formed document is:
  * document	   ::=   	prolog element Misc*
  *
  * But for sake of simplicity xml-lens defines it rather as:
  *
  * document     ::=    prolog element
  *
  * That means that comments and processing instructions that are placed after the root element cannot be
  * expressed using xml-lens AST. Mind that it does not apply to comments and processing instructions which
  * are placed outside of root element but before it. Those items can be expressed in terms of xml-lens AST
  * as part of Prolog.
  */
final case class XmlDocument(prolog: Prolog, root: LabeledElement)

object XmlDocument {
  def noProlog(root: LabeledElement) =
    XmlDocument(Prolog(None, Seq.empty, None), root)

  def withProlog(version: String, encoding: Option[String], root: LabeledElement) =
    XmlDocument(Prolog(Some(XmlDeclaration(version, encoding)), List.empty, None), root)
}
