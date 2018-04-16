package pl.msitko.xml.printing

import pl.msitko.xml.entities._
import CommonXmlWriter._

object XmlPrinter extends CommonXmlPrinter {
  override def print(doc: XmlDocument)(implicit cfg: PrinterConfig): String = {
    import InternalMonoid._

    var writer = CommonXmlWriter.writeProlog[StringBuilder](doc.prolog, cfg.eolAfterXmlDecl)(InternalMonoid[StringBuilder].zero)

    var toVisit = List[Node](doc.root)
    var toEnd = List.empty[LabeledElement]
    var level = 0
    val elementWriter = ElementWriter.forConfig(cfg)

    while (toVisit.nonEmpty) {
      val current = toVisit.head
      toVisit = toVisit.tail

      writer = current match {
        case elem: LabeledElement =>
          val r = elementWriter.writeElement(elem, level)(writer)
          level += 1
          val toAdd = elem.element.children
          toVisit = toAdd.toList ++ (null.asInstanceOf[Node] +: toVisit)
          toEnd = elem :: toEnd
          r
        case txt: Text =>
          writeText(txt)(writer, cfg)
        case pi: ProcessingInstruction =>
          writeProcessingInstruction(pi)(writer)
        case cdata: CData =>
          writeCData(cdata)(writer)
        case comment: Comment =>
          writeComment(comment)(writer)
        case entityRef: EntityReference =>
          writeEntityReference(entityRef)(writer)
        case null =>
          val r = elementWriter.writeEndElement(toEnd.head, level)(writer)
          level -= 1
          toEnd = toEnd.tail
          r
      }
    }

    writer.toString
  }
}
