package net.michalsitko.xml.printing
import net.michalsitko.xml.entities._
import net.michalsitko.xml.printing.CommonXmlWriter._

object XmlPrinter extends CommonXmlPrinter {
  override def print(doc: XmlDocument)(implicit cfg: PrinterConfig): String = {
    import InternalMonoid._

    var writer = CommonXmlWriter.writeProlog[String](doc.prolog, cfg.eolAfterXmlDecl)(InternalMonoid[String].zero)

    var toVisit = List[Node](doc.root)
    var toEnd = List.empty[LabeledElement]

    while (toVisit.nonEmpty) {
      val current = toVisit.head
      toVisit = toVisit.tail

      writer = current match {
        case elem: LabeledElement =>
          val r = writeElement(elem)(writer)
          val toAdd = elem.element.children
          toVisit = toAdd.toList ++ (null.asInstanceOf[Node] +: toVisit)
          toEnd = elem :: toEnd
          r
        case txt: Text =>
          writeText(txt)(writer)
        case pi: ProcessingInstruction =>
          writeProcessingInstruction(pi)(writer)
        case cdata: CData =>
          writeCData(cdata)(writer)
        case comment: Comment =>
          writeComment(comment)(writer)
        case entityRef: EntityReference =>
          writeEntityReference(entityRef)(writer)
        case null =>
          val r = writeEndElement(toEnd.head.label)(writer)
          toEnd = toEnd.tail
          r
      }
    }

    writer
  }
}
