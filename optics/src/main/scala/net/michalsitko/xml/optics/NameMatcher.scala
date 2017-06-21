package net.michalsitko.xml.optics

import net.michalsitko.xml.entities.ResolvedName

// TODO: should be sealed?
trait NameMatcher {
  def matches(resolvedName: ResolvedName): Boolean
  def toResolvedName: ResolvedName
}

object NameMatcher {
  // TODO: probably no longer needed?
  def fromString(matcher: String): NameMatcher = {
    IgnoreNamespaceMatcher(matcher)
  }
}

// TODO: think about making it package-private
final case class IgnoreNamespaceMatcher(localName: String) extends NameMatcher {
  override def matches(resolvedName: ResolvedName): Boolean =
    localName == resolvedName.localName

  // It is the most reasonable behavior, but still it may be quite surprising for users since
  // ignoring namespace for lookup purposes does not neccessarily mean that for e.g. replaceOrAddAttr
  // we want to add attribute without namespace
  // TODO: add documentation about it
  override def toResolvedName: ResolvedName =
    ResolvedName("", None, localName)
}

final case class ResolvedNameMatcher(uri: Option[String], localName: String) extends NameMatcher {
  // TODO: implement according to https://www.w3.org/TR/xml-names11/#NSNameComparison and
  // https://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
  override def matches(resolvedName: ResolvedName): Boolean =
    uri == resolvedName.uri && localName == resolvedName.localName

  override def toResolvedName: ResolvedName =
    ResolvedName("", uri, localName)
}

final case class Namespace(uri: Option[String]) {
  def name(localName: String): ResolvedNameMatcher =
    ResolvedNameMatcher(uri, localName)
}

object Namespace {
  def apply(uri: String): Namespace =
    Namespace(Some(uri))
}
