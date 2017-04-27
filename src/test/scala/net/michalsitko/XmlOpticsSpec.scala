package net.michalsitko

import net.michalsitko.utils.XmlFragments
import org.scalatest._

import scala.xml.{Elem, NodeSeq, Text, XML}

class XmlOpticsSpec extends FlatSpec with Matchers with XmlFragments {
  "naive solution" should "works for text replacement" in {
    val simpleXml = XML.loadString(simpleAsString)

    val res = simpleXml.map {
      case aElem: Elem if (aElem.label == "a") =>
        aElem.copy(child = aElem.child.flatMap {
          case c1Elem: Elem if (c1Elem.label == "c1") =>
            c1Elem.copy(child = c1Elem.child.flatMap {
              case fElem: Elem if (fElem.label == "f") =>
                fElem.copy(child = List(Text("f replaced")))
              case el => el
            })
          case el => el
        })
      case el => el
    }

    val expectedXml = XML.loadString(ExpectedValues.simpleAsStringAfterTextReplacement)

    println("bazinga1: " + (res \ "c1").size + ", " + (expectedXml \ "c1").size)
    println("bazinga1e: " + ((res \ "c1") == (expectedXml \ "c1")))
    println("bazinga2: " + (res \ "c2").size + ", " + (expectedXml \ "c2").size)
    println("bazinga2e: " + ((res \ "c2") == (expectedXml \ "c2")))
    println("bazinga3: " + (res \ "s").size + ", " + (expectedXml \ "s").size)
    println("bazinga3e: " + ((res \ "s") == (expectedXml \ "s")))

    res.head should equal(expectedXml)
  }
}

object ExpectedValues {
  val simpleAsStringAfterTextReplacement =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>f replaced</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f>f replaced</f>
      |      <h>item2</h>
      |   </c1>
      |   <c2>
      |      <f>item1</f>
      |      <g>item2</g>
      |      <h>item3</h>
      |   </c2>
      |   <s>summary</s>
      |</a>
    """.stripMargin
}
