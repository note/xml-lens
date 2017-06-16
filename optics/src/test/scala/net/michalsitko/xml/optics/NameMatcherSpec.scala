package net.michalsitko.xml.optics

import net.michalsitko.xml.entities.ResolvedName

class NameMatcherSpec extends WordSpec with Matchers {
  "IgnoreNamespaceMatcher" should {
    "work" in {
      val matcher = IgnoreNamespaceMatcher("elem")

      val name = ResolvedName("abc", Some("http://abc.com"), "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(prefix = "def")) should equal(true)
      matcher.matches(name.copy(uri = None)) should equal(true)
      matcher.matches(name.copy(localName = "hello")) should equal(false)
    }

    "be case sensitive" in {
      val matcher = IgnoreNamespaceMatcher("elem")

      val name = ResolvedName("abc", Some("http://abc.com"), "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(localName = "Elem")) should equal(false)
      matcher.matches(name.copy(localName = "ELEM")) should equal(false)
    }
  }

  "PrefixMatcher" should {
    "work" in {
      val matcher = PrefixMatcher("p", "elem")

      val name = ResolvedName("p", Some("http://p.com"), "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(prefix = "def")) should equal(false)
      matcher.matches(name.copy(uri = Some("http://w.com"))) should equal(true)
      matcher.matches(name.copy(uri = None)) should equal(true)
      matcher.matches(name.copy(localName = "elem2")) should equal(false)
    }

    "be case sensitive in regard to prefix" in {
      val matcher = PrefixMatcher("p", "elem")

      val name = ResolvedName("p", Some("http://p.com"), "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(prefix = "P")) should equal(false)
    }

    "be case sensitive in regard to local name" in {
      val matcher = PrefixMatcher("p", "elem")

      val name = ResolvedName("p", Some("http://p.com"), "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(localName = "Elem")) should equal(false)
      matcher.matches(name.copy(localName = "ELEM")) should equal(false)
    }
  }

  "ResolvedNameMatcher" should {
    "work" in {
      val matcher = ResolvedNameMatcher(Some("http://a.com"), "elem")

      val name = ResolvedName("a", Some("http://a.com"), "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(prefix = "abc")) should equal(true)
      matcher.matches(name.copy(uri = Some("http://b.com"))) should equal(false)
      matcher.matches(name.copy(uri = None)) should equal(false)
      matcher.matches(name.copy(localName = "elem2")) should equal(false)
    }

    "be case sensitive in regard to namespace" in {
      val matcher = ResolvedNameMatcher(Some("http://a.com"), "elem")

      val name = ResolvedName("a", Some("http://a.com"), "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(uri = Some("http://A.com"))) should equal(false)
      matcher.matches(name.copy(uri = Some("HTTP://A.COM"))) should equal(false)
    }

    "be case sensitive in regard to localName" in {
      val matcher = ResolvedNameMatcher(Some("http://a.com"), "elem")

      val name = ResolvedName("a", Some("http://a.com"), "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(localName = "Elem")) should equal(false)
      matcher.matches(name.copy(localName = "ELEM")) should equal(false)
    }
  }
}
