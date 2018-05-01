package pl.msitko.xml.matchers

import pl.msitko.xml.entities.ResolvedName

trait NameMatcher {
  def matches(resolvedName: ResolvedName): Boolean
}

trait ToResolvedName {
  def toResolvedName: ResolvedName
}

object NameMatcher {
  def fromString(matcher: String): IgnoreNamespaceMatcher = {
    IgnoreNamespaceMatcher(matcher)
  }
}

final case class IgnoreNamespaceMatcher(localName: String) extends NameMatcher with ToResolvedName {
  override def matches(resolvedName: ResolvedName): Boolean =
    localName == resolvedName.localName

  /**
    * It is the most reasonable behavior, but still it may be quite surprising for users since
    * ignoring namespace for lookup purposes does not neccessarily mean that for e.g. replaceOrAddAttr
    * we want to add attribute without namespace
    */
  override def toResolvedName: ResolvedName =
    ResolvedName.unprefixed(localName)
}

final case class ResolvedNameMatcher(uri: String, localName: String) extends NameMatcher {
  /**
    * According to https://www.w3.org/TR/xml-names11/#NSNameComparison:
    *  [Definition: The two IRIs are treated as strings, and they are identical if and only if the strings are identical,
    *  that is, if they are the same sequence of characters. ] The comparison is case-sensitive, and no %-escaping is
    *  done or undone.
    *
    * This simple implementation meets those criteria. Later on in the same document one more thing is mentioned:
    *   In a namespace declaration, the IRI reference is the normalized value of the attribute, so replacement of XML character and entity references has already been done before any comparison.
    *
    * BEWARE that this thing is not done in that simple implementation
    *
    * https://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references might be of interest in
    * case of future implementations
    */
  override def matches(resolvedName: ResolvedName): Boolean =
    uri == resolvedName.uri && localName == resolvedName.localName
}

final case class Namespace(uri: String) {
  def name(localName: String): ResolvedNameMatcher =
    ResolvedNameMatcher(uri, localName)
}

object Namespace {
  val empty: Namespace =
    Namespace("")
}

final case class PrefixedResolvedNameMatcher(prefix: String, uri: String, localName: String) extends NameMatcher with ToResolvedName {
  /**
    * @see [[ResolvedNameMatcher.matches]]
    */
  override def matches(resolvedName: ResolvedName): Boolean =
    uri == resolvedName.uri && localName == resolvedName.localName

  override def toResolvedName: ResolvedName =
    ResolvedName(prefix, uri, localName)
}

final case class PrefixedNamespace(prefix: String, uri: String) {
  def name(localName: String): PrefixedResolvedNameMatcher =
    PrefixedResolvedNameMatcher(prefix, uri, localName)
}
