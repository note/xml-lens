package net.michalsitko

import monocle.{PTraversal, Traversal}
import net.michalsitko.utils.XmlFragments
import org.scalatest._

import scala.xml._

class XmlOpticsSpec extends WordSpec with Matchers with XmlFragments with Solutions {
  "naive solution" should {
    "work for text replacement" in {
      val simpleXml = XML.loadString(simpleAsString)

      val res = naive(simpleXml)

      val expectedXml = XML.loadString(ExpectedValues.simpleAsStringAfterTextReplacement)
      res.head should equal(expectedXml)
    }
  }

  "naiveXmlSupport" should {
    "work for text replacement" in {
      val simpleXml = XML.loadString(simpleAsString)

      val res = naiveXmlSupport(simpleXml)

      val expectedXml = XML.loadString(ExpectedValues.simpleAsStringAfterTextReplacement)
      res.head should equal(expectedXml)
    }

    "work for more complicated text replacement" in {
      val simpleXml = XML.loadString(xmlAsString)

      val res = naiveXmlSupport2(simpleXml)

      val expectedXml = XML.loadString(ExpectedValues.xmlAsStringAfterTextReplacement)
      res.head should equal(expectedXml)
    }
  }

  "Optics" should {
    "work for text replacement" in {
      val simpleXml = XML.loadString(simpleAsString)

      val res = withOptics(simpleXml)

      val expectedXml = XML.loadString(ExpectedValues.simpleAsStringAfterTextReplacement)
      res should equal(expectedXml)
    }

    "work for more complicated text replacement" in {
      val simpleXml = XML.loadString(xmlAsString)

      val res = withOptics2(simpleXml)

      val expectedXml = XML.loadString(ExpectedValues.xmlAsStringAfterTextReplacement)
      res should equal(expectedXml)
    }

    // TODO: to remove
    "tmp1" in {
      import net.michalsitko.optics.Optics._

      val simpleXml = XML.loadString(xmlString1)

      val focused = nodeLens("c1").composeLens(nodeLens2("f")).set(NodeSeq.fromSeq(Seq.empty))
      val res = focused(simpleXml)

      val expectedXml = XML.loadString("""<?xml version="1.0" encoding="UTF-8"?>
                                         |<a>
                                         |   <c1>
                                         |
                                         |   </c1>
                                         |</a>
                                       """.stripMargin)

      val trimmedEqual = scala.xml.Utility.trim(res) == scala.xml.Utility.trim(expectedXml)

      trimmedEqual should equal(true)
    }

    "tmp2" in {
      import net.michalsitko.optics.Optics._

      val simpleXml = XML.loadString(xmlString1)

      val newElem = <d>txt</d>
      val focused = nodeLens("c1").composeLens(nodeLens2("f")).set(NodeSeq.fromSeq(List(newElem)))
      val res = focused(simpleXml)

      val expectedXml = XML.loadString("""<?xml version="1.0" encoding="UTF-8"?>
                                         |<a>
                                         |   <c1>
                                         |   <d>txt</d>
                                         |   </c1>
                                         |</a>
                                       """.stripMargin)

      val trimmedEqual = scala.xml.Utility.trim(res) == scala.xml.Utility.trim(expectedXml)

      trimmedEqual should equal(true)
    }

    "tmp3" in {
      import net.michalsitko.optics.Optics._

      val simpleXml = XML.loadString(xmlString2)

      val newElem = NodeSeq.fromSeq(List(<d>txt</d>))
      val focused = nodeLens("c1").composeLens(nodeLens2("f"))
      val res = focused.set(newElem)(simpleXml)

      val newF = focused.get(simpleXml)

      newF should equal(newElem)
    }
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
    import net.michalsitko.naive.XmlSupport._
    elem.map {
      deeper("a")(deeper("c1")(update("f"){
        case elem: Elem => elem.copy(child = List(Text("f replaced")))
      }))
    }.head
  }

  def naiveXmlSupport2(elem: Elem): NodeSeq = {
    import net.michalsitko.naive.XmlSupport._
    elem.map {
      deeper("a")(deeper("b")(deeper("c")(deeper("d")(deeper("e2")(update("f"){
        case elem: Elem => elem.copy(child = List(Text("f replaced")))
      })))))
    }.head
  }

  def withOptics(element: Elem): NodeSeq = {
    import net.michalsitko.optics.Optics._

    val focused = (nodeLens("c1").composeLens(nodeLens2("f"))).composeTraversal(each.composePrism(elemPrism))
    focused.modify(_.copy(child = List(Text("f replaced"))))(element)
  }

  def withOptics2(element: Elem): NodeSeq = {
    import net.michalsitko.optics.Optics._

    val composed =
      nodeLens("b").composeLens(nodeLens2("c")).composeLens(nodeLens2("d")).composeLens(nodeLens2("e2")).composeLens(nodeLens2("f"))
    val focused = composed.composeTraversal(each.composePrism(elemPrism))
    focused.modify(_.copy(child = List(Text("f replaced"))))(element)
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

  val xmlAsStringAfterTextReplacement =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |  <b>
      |    <c>
      |      <d>
      |        <e1>
      |          <f>item1</f>
      |          <g>item2</g>
      |        </e1>
      |        <e2>
      |          <f>f replaced</f>
      |          <g>item2</g>
      |          <h>item3</h>
      |          <f>f replaced</f>
      |        </e2>
      |        <e2>
      |          <f>f replaced</f>
      |          <g>item2</g>
      |          <h>item3</h>
      |          <f>f replaced</f>
      |        </e2>
      |      </d>
      |      <s>summary</s>
      |    </c>
      |  </b>
      |</a>
    """.stripMargin
}
