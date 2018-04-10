package pl.msitko.xml.optics

import pl.msitko.xml.BasicSpec
import pl.msitko.xml.matchers.Namespace
import pl.msitko.xml.optics.ElementOptics.deeper

trait NamespaceSpec extends BasicSpec {
  "Optics" should {
    "should respect namespaces" in new Context {
      val xml = parse(input)

      val ignoreNs = withCriteria(deeper("f"))
      val withNsA = {
        val ns = Namespace("http://a.com")
        withCriteria(deeper(ns.name("f")))
      }
      val withNsB = {
        val ns = Namespace("http://b.com")
        withCriteria(deeper(ns.name("f")))
      }
      val withNsC = {
        val ns = Namespace("http://c.com")
        withCriteria(deeper(ns.name("f")))
      }

      ignoreNs.getAll(xml) should ===(List("a.com", "b.com", "c.com", "d.com"))
      withNsA.getAll(xml) should ===(List("a.com"))
      withNsB.getAll(xml) should ===(List("b.com"))
      withNsC.getAll(xml) should ===(List("c.com"))
    }

    "should allow to use global namespace" in new Context {
      val xml = parse(input2)

      val defaultNs = {
        val ns = Namespace.empty
        withCriteria(deeper(ns.name("f")))
      }

      defaultNs.getAll(xml) should ===(List("no namespace"))
    }

  }
}
