package net.michalsitko.xml.optics

import net.michalsitko.xml.entities.ResolvedName

// TODO: should be sealed?
trait NameMatcher {
  def matches(resolvedName: ResolvedName): Boolean
}

object NameMatcher {
  def fromString(matcher: String): NameMatcher = {
    val parts = matcher.split(':')
    if (parts.size == 2) {
      PrefixMatcher(parts(0), parts(1))
    } else {
      IgnoreNamespaceMatcher(parts(0))
    }
  }
}

// TODO: think about making it package-private
final case class IgnoreNamespaceMatcher(localName: String) extends NameMatcher {
  override def matches(resolvedName: ResolvedName): Boolean =
    localName == resolvedName.localName
}

// for no prefix empty string is used
final case class PrefixMatcher(prefix: String, localName: String) extends NameMatcher {
  override def matches(resolvedName: ResolvedName): Boolean =
    prefix == resolvedName.prefix && localName == resolvedName.localName
}

final case class ResolvedNameMatcher(uri: Option[String], localName: String) extends NameMatcher {
  // TODO: implement according to https://www.w3.org/TR/xml-names11/#NSNameComparison and
  // https://en.wikipedia.org/wiki/List_of_XML_and_HTML_character_entity_references
  override def matches(resolvedName: ResolvedName): Boolean =
    uri == resolvedName.uri && localName == resolvedName.localName
}

final case class Namespace(uri: Option[String]) {
  def name(localName: String): ResolvedNameMatcher =
    ResolvedNameMatcher(uri, localName)
}

object Namespace {
  def apply(uri: String): Namespace =
    Namespace(Some(uri))
}
