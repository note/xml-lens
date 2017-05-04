package net.michalsitko

import monocle.{PTraversal, Traversal}
import net.michalsitko.utils.XmlFragments
import org.scalatest._

import scala.xml._

class XmlOpticsSpec extends FlatSpec with Matchers with XmlFragments with Solutions {
  "naive solution" should "works for text replacement" in {
    val simpleXml = XML.loadString(simpleAsString)

    val res = naive(simpleXml)

    val expectedXml = XML.loadString(ExpectedValues.simpleAsStringAfterTextReplacement)
    res.head should equal(expectedXml)
  }

  "naiveXmlSupport" should "works for text replacement" in {
    val simpleXml = XML.loadString(simpleAsString)

    val res = naiveXmlSupport(simpleXml)

    val expectedXml = XML.loadString(ExpectedValues.simpleAsStringAfterTextReplacement)
    res.head should equal(expectedXml)
  }

  "with Optics" should "works for text replacement" in {
    val simpleXml = XML.loadString(simpleAsString)

    val res = withOptics(simpleXml)

    val expectedXml = XML.loadString(ExpectedValues.simpleAsStringAfterTextReplacement)
    res should equal(expectedXml)
  }


}

trait Solutions {
  def naive(elem: Elem): NodeSeq = {
    elem.map {
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
  }

  def naiveXmlSupport(elem: Elem): NodeSeq = {
    import net.michalsitko.utils.XmlSupport._
    elem.map {
      deeper("a")(deeper("c1")(update("f"){
        case elem: Elem => elem.copy(child = List(Text("f replaced")))
      }))
    }.head
  }

  def withOptics(elem: Elem): NodeSeq = {
    /**
      * WSZYSTKO ZLE ROBILEM!
      * musze miec nodeSeqLens = Lens[Element, NodeSeq] - ten lens zawsze sie powiedzie w sensie
      * nawet jesli nie ma elementu "EL" to nodeSeqLens("EL") zwroci po prostu pusty NodeSeq
      * I teraz jesli chce dokonwyac jakis operacji na wyniku tego lensa to musze zdecydowac o semantyce wolajac
      * np. metode "each", ktora zwroci Traversala
      *
      *
      */
    import net.michalsitko.optics.Optics2._

    val focused = (nodeLens("c1").composeLens(nodeLens2("f"))).composeTraversal(each.composePrism(elemPrism))
    focused.modify(_.copy(child = List(Text("f replaced"))))(elem)
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
