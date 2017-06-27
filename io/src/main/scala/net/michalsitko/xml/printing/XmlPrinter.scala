package net.michalsitko.xml.printing

import net.michalsitko.xml.entities._

// TODO: whole XmlPrinter needs rethinking
// current version is very naive. It assumes that input for `print` is basically a result of XmlParser.parse
// which is not true in general. User can manipulate AST in any way, so we should take care of undefined namespaces' prefixes,
// isRepairingNamespaces, escaping special characters
object XmlPrinter {
  def print(elem: LabeledElement): String = {
    val writer = new SimpleXmlWriter
    newLoop2(elem, writer)
    writer.build
  }

  def prettyPrint(elem: LabeledElement, config: PrinterConfig): String = {
    val writer = new PrettyXmlWriter(config)
    newLoop2(elem, writer)
    writer.build
  }

  def newLoop2(root: LabeledElement, writer: XmlWriter) = {
    var toVisit = List[Node](root)
    var toEnd = List.empty[LabeledElement]

    while(toVisit.nonEmpty) {
      val current = toVisit.head
      toVisit = toVisit.tail
      current match {
        case elem: LabeledElement =>
          writer.writeLabeled(elem)
          val toAdd = elem.element.children
          toVisit = toAdd.toList ++ (null.asInstanceOf[Node] +: toVisit)
          toEnd = elem :: toEnd

        case text: Text =>
          writer.writeText(text)

        case comment: Comment =>
          writer.writeComment(comment)

        case null =>
          writer.writeEndElement(toEnd.head)
          toEnd = toEnd.tail
      }
    }
  }
}
