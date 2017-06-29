package net.michalsitko.xml.optics

import net.michalsitko.xml.entities.ResolvedName
import org.scalatest.{Matchers, WordSpec}

class NameMatcherSpec extends WordSpec with Matchers {
  "IgnoreNamespaceMatcher" should {
    "work" in {
      val matcher = IgnoreNamespaceMatcher("elem")

      val name = ResolvedName("abc", "http://abc.com", "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(prefix = "def")) should equal(true)
      matcher.matches(name.copy(uri = "")) should equal(true)
      matcher.matches(name.copy(localName = "hello")) should equal(false)
    }

    "be case sensitive" in {
      val matcher = IgnoreNamespaceMatcher("elem")

      val name = ResolvedName("abc", "http://abc.com", "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(localName = "Elem")) should equal(false)
      matcher.matches(name.copy(localName = "ELEM")) should equal(false)
    }
  }

  "ResolvedNameMatcher" should {
    "work" in {
      val matcher = ResolvedNameMatcher("http://a.com", "elem")

      val name = ResolvedName("a", "http://a.com", "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(prefix = "abc")) should equal(true)
      matcher.matches(name.copy(uri = "http://b.com")) should equal(false)
      matcher.matches(name.copy(uri = "")) should equal(false)
      matcher.matches(name.copy(localName = "elem2")) should equal(false)
    }

    "be case sensitive in regard to namespace" in {
      val matcher = ResolvedNameMatcher("http://a.com", "elem")

      val name = ResolvedName("a", "http://a.com", "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(uri = "http://A.com")) should equal(false)
      matcher.matches(name.copy(uri = "HTTP://A.COM")) should equal(false)
    }

    "be case sensitive in regard to localName" in {
      val matcher = ResolvedNameMatcher("http://a.com", "elem")

      val name = ResolvedName("a", "http://a.com", "elem")

      matcher.matches(name) should equal(true)
      matcher.matches(name.copy(localName = "Elem")) should equal(false)
      matcher.matches(name.copy(localName = "ELEM")) should equal(false)
    }
  }
}
