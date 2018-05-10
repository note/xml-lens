package pl.msitko.xml.bench

import java.io.StringWriter

import scala.xml.{Elem, Text, XML}

object SimpleTransformationStd extends SimpleTransformation {
  def transform(el: Elem): Elem = {
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
  }

  override def transform(input: String): String = {
    val xml = XML.loadString(input)

    val transformed = xml.map {
      case el: Elem if el.label == "a" =>
        el.copy(child = el.child.flatMap {
          case el: Elem if el.label == "interesting" =>
            el.copy(child = el.child.flatMap {
              case el: Elem if el.label == "special" =>
                transform(el)
              case a => a
            })
          case a => a
        })
      case a => a
    }

    val writer = new StringWriter

    XML.write(writer, transformed.head, "UTF-8", true, null)
    writer.toString
  }
}
