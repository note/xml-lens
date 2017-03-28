package net.michalsitko

import net.michalsitko.utils.{XmlFragments, XmlSupport}

import scala.collection.immutable.Seq
import scala.xml._
import scalaz.Lens

object WrongOptics2 extends AnyRef with XmlFragments with XmlSupport with Optics2 {
  def main(args: Array[String]): Unit = {

    // let's start with simpleAsString
    val simpleXml: Elem = XML.loadString(verySimpleString)

    // it somehow works but only for verySimpleString.
    // Run it with simpleAsString to see that it does not work properly for more complicated cases.
    val lensRes = {
//      val aLens = lens("a")
      val c1Lens = lens("c1")
      val fLens = lens("f")
      val wholeLens = (c1Lens >=> fLens)
      println("res of get: " + wholeLens.get(simpleXml))
      println("res -------------")
      val res2 = wholeLens.mod(el => el.copy(label = "replaced"), simpleXml)
      println("res2: " + res2)
    }

  }
}

trait Optics2 {
  def lens(label: String): Lens[Elem, Elem] = Lens.lensu[Elem, Elem] (
    (element, newValue) => {
      println(s"bazinga set [$label]: $element")
      element.flatMap {
        case parent: Elem =>
          parent.copy(child = parent.child.flatMap {
            case el: Elem if el.label == label => newValue
            case el => el
          })
        case el => el
      }.collectFirst {
        case el: Elem => el
      }.get
    },
    element => {
      (element \ label).collectFirst {
        case el: Elem => el
      }.get
    }
  )
}
