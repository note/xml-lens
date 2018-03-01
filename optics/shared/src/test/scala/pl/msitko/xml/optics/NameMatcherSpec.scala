package pl.msitko.xml.optics

import pl.msitko.xml.entities.ResolvedName
import pl.msitko.xml.test.utils.BaseSpec

class NameMatcherSpec extends BaseSpec {
  "IgnoreNamespaceMatcher" should {
    "work" in {
      val matcher = IgnoreNamespaceMatcher("elem")

      val name = ResolvedName("abc", "http://abc.com", "elem")

      matcher.matches(name) should === (true)
      matcher.matches(name.copy(prefix = "def")) should === (true)
      matcher.matches(name.copy(uri = "")) should === (true)
      matcher.matches(name.copy(localName = "hello")) should === (false)
    }

    "be case sensitive" in {
      val matcher = IgnoreNamespaceMatcher("elem")

      val name = ResolvedName("abc", "http://abc.com", "elem")

      matcher.matches(name) should === (true)
      matcher.matches(name.copy(localName = "Elem")) should === (false)
      matcher.matches(name.copy(localName = "ELEM")) should === (false)
    }
  }

  "ResolvedNameMatcher" should {
    "work" in {
      val matcher = ResolvedNameMatcher("http://a.com", "elem")

      val name = ResolvedName("a", "http://a.com", "elem")

      matcher.matches(name) should === (true)
      matcher.matches(name.copy(prefix = "abc")) should === (true)
      matcher.matches(name.copy(uri = "http://b.com")) should === (false)
      matcher.matches(name.copy(uri = "")) should === (false)
      matcher.matches(name.copy(localName = "elem2")) should === (false)
    }

    "be case sensitive in regard to namespace" in {
      val matcher = ResolvedNameMatcher("http://a.com", "elem")

      val name = ResolvedName("a", "http://a.com", "elem")

      matcher.matches(name) should === (true)
      matcher.matches(name.copy(uri = "http://A.com")) should === (false)
      matcher.matches(name.copy(uri = "HTTP://A.COM")) should === (false)
    }

    "be case sensitive in regard to localName" in {
      val matcher = ResolvedNameMatcher("http://a.com", "elem")

      val name = ResolvedName("a", "http://a.com", "elem")

      matcher.matches(name) should === (true)
      matcher.matches(name.copy(localName = "Elem")) should === (false)
      matcher.matches(name.copy(localName = "ELEM")) should === (false)
    }
  }
}
