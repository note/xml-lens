package net.michalsitko.xml.bench

import java.io.StringWriter

import scala.xml.{Elem, Node, Text, XML}

object SimpleTransformationStd extends SimpleTransformation {
  val doTransform: PartialFunction[Node, Node] = {
    case el: Elem if el.label == "f" =>
      if(el.child.size == 1) {
        val replaceWith = el.child.head match {
          case t: Text =>
            Text(t.text.toUpperCase)
          case a => a
        }
        el.copy(child = List(replaceWith))
      } else {
        el
      }
    case a => a
  }

  override def transform(input: String): String = {
    val xml = XML.loadString(input)

    val transformed = xml.map {
      case el: Elem if el.label == "a" =>
        el.copy(child = el.child.flatMap { el =>
          doTransform(el)
        })
      case a => a
    }

    val writer = new StringWriter

    XML.write(writer, transformed.head, "UTF-8", true, null)
    val res = writer.toString
    res
  }
}
