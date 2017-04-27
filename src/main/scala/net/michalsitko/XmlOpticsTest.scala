package net.michalsitko

import monocle.{POptional, Prism}
import net.michalsitko.WrongOptics2.verySimpleString
import net.michalsitko.optics.XmlOptics
import net.michalsitko.utils.XmlFragments

import scala.xml.{Elem, XML}

object XmlOpticsTest extends XmlFragments with XmlOptics {
  def main(args: Array[String]): Unit = {
    val xml: Elem = XML.loadString(simpleAsString)
//
//    val c1 = (xml \ "c1")
//    c1 match {
//      case el: Elem =>
//        println("it's elem")
//      case n =>
//        println("it's not elem: " + n.getClass)
//    }
//
//    println("bazinga")
//    c1.map {
//      case el: Elem =>
//        println("it's elem")
//      case n =>
//        println("it's not elem: " + n)
//    }

//    val f: POptional[Elem, Elem, Elem, Elem] =
//      deeper("c1").composePrism(toElem).composeLens(deeper("f")).composePrism(toElem)
//    val f: POptional[Elem, Elem, Elem, Elem] =
//      deeper("c1").composePrism(toElem).composeOptional(deeper("f").composePrism(toElem))
//    val res = f.modify(el => el.copy(label = "modified"))(xml)

//    println("res: " + res)

//    val res = partialRoundTripOneWay(toElem, (xml \ "c1").head)
//    println("res: " + res)
//  }
//
//  def partialRoundTripOneWay[S, A](p: Prism[S, A], s: S): Boolean =
//    p.getOption(s) match {
//      case None    => true // nothing to prove
//      case Some(a) => p.reverseGet(a) == s
    }
}
