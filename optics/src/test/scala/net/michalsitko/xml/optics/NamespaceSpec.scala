package net.michalsitko.xml.optics

import monocle.Traversal
import net.michalsitko.xml.entities.Element
import net.michalsitko.xml.optics.ElementOptics._
import net.michalsitko.xml.optics.XmlDocumentOptics._
import net.michalsitko.xml.parsing.XmlParser
import org.scalatest.{Matchers, WordSpec}

class NamespaceSpec extends WordSpec with Matchers {
  "Optics" should {
    "should respect namespaces" in new Context {
      val xml = XmlParser.parse(input).right.get

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
      val xml = XmlParser.parse(input2).right.get

      val defaultNs = {
        val ns = Namespace.empty
        withCriteria(deeper(ns.name("f")))
      }

      defaultNs.getAll(xml) should ===(List("no namespace"))
    }

  }
}

trait Context {
  val input =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns="http://a.com" xmlns:b="http://b.com">
      |   <c1>
      |      <f>a.com</f>
      |      <b:f>b.com</b:f>
      |   </c1>
      |   <c1 xmlns="http://c.com" xmlns:b="http://d.com">
      |      <f>c.com</f>
      |      <b:f>d.com</b:f>
      |   </c1>
      |</a>""".stripMargin

  val input2 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns:b="http://b.com">
      |   <c1>
      |      <f>no namespace</f>
      |      <b:f>b.com</b:f>
      |   </c1>
      |</a>""".stripMargin

  def withCriteria(criteria: Traversal[Element, Element]) = {
    import ElementOptics._
    import LabeledElementOptics._

    rootLens.composeTraversal(deep("c1").composeTraversal(criteria).composeOptional(hasTextOnly))
  }
}