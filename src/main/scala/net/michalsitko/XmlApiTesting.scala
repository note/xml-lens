package net.michalsitko

import net.michalsitko.utils.XmlFragments

import scala.xml.{Elem, Node, NodeSeq, XML}

object XmlApiTesting extends AnyRef with XmlFragments {
  def main(args: Array[String]): Unit = {
    println("testing")

    val xml: Elem = XML.loadString(xmlAsString)
    val res: NodeSeq = (xml \ "xyz")
    val first: Node = res.head
  }

//  val a = monocle.Optional[NodeSeq, NodeSeq]

}


