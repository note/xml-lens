package net.michalsitko.xml.entities

sealed trait Node

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

case class Text(text: String) extends Node

// TODO: think if Seq[Attribute] is a good choice taking into account that attribute names have to be unique within
// single element and printing with XmlStreamWriter a non-unique Attribute will throw an exception
case class Element(attributes: Seq[Attribute], children: Seq[Node], namespaceDeclarations: Seq[NamespaceDeclaration])
object Element {
  def empty: Element = Element(Seq.empty, Seq.empty, Seq.empty)
}

// when no prefix in XML then: prefix == ""
// TODO: investigate why in scala-xml Attribute value is defined as `value: Seq[Node]`
// also, take a look at: https://www.w3.org/TR/xml/#NT-AttValue
case class Attribute(key: ResolvedName, value: String) {
  def sameKey(anotherKey: ResolvedName): Boolean = {
    (key.prefix == anotherKey.prefix) && (key.localName == anotherKey.localName)
  }
}
// TODO: maybe should be changed to:
// Attribute(ResolvedName, String)

object Attribute {
  def unprefixed(key: String, value: String): Attribute =
    Attribute(ResolvedName.unprefixed(key), value)
}

// should prefix and/or uri be optional?
case class ResolvedName(prefix: String, uri: Option[String], localName: String) {
  def hasPrefix: Boolean = prefix.nonEmpty
}

object ResolvedName {
  def unprefixed(localName: String): ResolvedName =
    ResolvedName("", None, localName)
}

case class NamespaceDeclaration(prefix: Option[String], uri: String)

// TODO: hierarchy is not comprehensive - it misses PCDATA, Entity References, Comments among the others

object SomeExample {
  // try to define following examplary XML
  """<?xml version="1.0" encoding="UTF-8"?>
    |<a>
    |   <c1>
    |      <f>item1</f>
    |      <g>item2</g>
    |   </c1>
    |   <c1>
    |      <f>item1</f>
    |      <h>item2</h>
    |   </c1>
    |   <c2>
    |      <f>item1</f>
    |      <g>item2</g>
    |      <h>item3</h>
    |   </c2>
    |   <s>summary</s>
    |</a>
  """.stripMargin

}