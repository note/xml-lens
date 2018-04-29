package pl.msitko.xml.printing

import Syntax._

// https://stackoverflow.com/questions/1091945/what-characters-do-i-need-to-escape-in-xml-documents
private [printing] object Escaper {
  // instead of simplistic `.replace("&", "&amp;")` (now rewritten in more performant fashion but the essence remains)
  // which is incorrect as will do substitution even
  // for entity references we should consider using sth like following regex:
  // val raw = raw"\&(?![a-zA-Z:_][a-zA-Z0-9:_\-\.]*;)".r
  // it's not used at the moment as the effect on performance is unknown (esp. relevant for escaping text
  // nodes which may be huge
  // I noticed javax.xml.stream.XMLStreamWriter does the same naive thing Escaper does here so
  // probably it's not that bad
  def escapeAttributeValue[M : InternalMonoid](value: String)(writer: M): M = {
    val needEscaping = value.exists {
      case '&' | '<' | '>' | '\"' => true
      case _ => false
    }

    if (needEscaping) {
      value.foldLeft(writer) { (acc, ch) =>
        ch match {
          case '&'    => writer.combine("&amp;")
          case '<'    => writer.combine("&lt;")
          case '>'    => writer.combine("&gt;")
          case '\"'   => writer.combine("&quot;")
          case ch     => writer.combine(ch)
        }
      }
    } else {
      writer.combine(value)
    }
  }

  def escapeText[M : InternalMonoid](text: String)(writer: M): M = {
    val needEscaping = text.exists {
      case '&' | '<' | '>' => true
      case _ => false
    }

    if (needEscaping) {
      text.foldLeft(writer) { (acc, ch) =>
        ch match {
          case '&'    => writer.combine("&amp;")
          case '<'    => writer.combine("&lt;")
          case '>'    => writer.combine("&gt;")
          case ch     => writer.combine(ch)
        }
      }
    } else {
      writer.combine(text)
    }
  }
}
