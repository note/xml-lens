package pl.msitko.xml.optics

import pl.msitko.xml.entities.ResolvedName

// TODO: should be sealed?
trait NameMatcher {
  def matches(resolvedName: ResolvedName): Boolean
}

trait ToResolvedName {
  def toResolvedName: ResolvedName
}

// TODO: should be package-private?
object NameMatcher {
  // TODO: probably no longer needed?
  def fromString(matcher: String): IgnoreNamespaceMatcher = {
    IgnoreNamespaceMatcher(matcher)
  }
}

// TODO: think about making it package-private
final case class IgnoreNamespaceMatcher(localName: String) extends NameMatcher with ToResolvedName {
  override def matches(resolvedName: ResolvedName): Boolean =
    localName == resolvedName.localName

  // It is the most reasonable behavior, but still it may be quite surprising for users since
  // ignoring namespace for lookup purposes does not neccessarily mean that for e.g. replaceOrAddAttr
  // we want to add attribute without namespace
  // TODO: add documentation about it
  override def toResolvedName: ResolvedName =
    ResolvedName.unprefixed(localName)
}

// TODO: probably should be package-private
final case class ResolvedNameMatcher(uri: String, localName: String) extends NameMatcher {
  // TODO: implement according to https://www.w3.org/TR/xml-names11/#NSNameComparison and
  // https://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
  override def matches(resolvedName: ResolvedName): Boolean =
    uri == resolvedName.uri && localName == resolvedName.localName
}

// TODO: probably should be package-private
final case class PrefixedResolvedNameMatcher(prefix: String, uri: String, localName: String) extends NameMatcher with ToResolvedName {
  // TODO: implement according to https://www.w3.org/TR/xml-names11/#NSNameComparison and
  // https://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
  override def matches(resolvedName: ResolvedName): Boolean =
  uri == resolvedName.uri && localName == resolvedName.localName

  override def toResolvedName: ResolvedName =
    ResolvedName(prefix, uri, localName)
}

final case class Namespace(uri: String) {
  def name(localName: String): ResolvedNameMatcher =
    ResolvedNameMatcher(uri, localName)
}

object Namespace {
  val empty: Namespace =
    Namespace("")
}

final case class PrefixedNamespace(prefix: String, uri: String) {
  def name(localName: String): PrefixedResolvedNameMatcher =
    PrefixedResolvedNameMatcher(prefix, uri, localName)
}
