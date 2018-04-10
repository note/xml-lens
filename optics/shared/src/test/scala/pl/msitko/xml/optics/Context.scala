package pl.msitko.xml.optics

import monocle.Traversal
import pl.msitko.xml.entities.Element
import pl.msitko.xml.optics.XmlDocumentOptics.rootLens

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
