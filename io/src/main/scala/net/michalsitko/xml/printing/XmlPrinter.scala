package net.michalsitko.xml.printing

import java.io.StringWriter
import net.michalsitko.xml.entities._

object XmlPrinter {
  val DefaultPrinterConfig = PrinterConfig(Indent.Remain)

  def print(doc: XmlDocument)(implicit cfg: PrinterConfig = DefaultPrinterConfig): String = {
    val stringOutput = new StringWriter()
    val writer = cfg.indent match {
      case _: Indent.IndentWith  => new PrettyXmlWriter(stringOutput, cfg)
      case Indent.Remain         => new JavaXmlWriter(stringOutput, cfg)
    }

    writer.writeProlog(doc.prolog)
    writeLabeledElement(doc.root, writer)

    val res = stringOutput.toString()
    stringOutput.close() // has no effect because of nature of StringWriter, but let's keep it in case of change
    res
  }

  private def writeLabeledElement(root: LabeledElement, writer: XmlWriter): Unit = {
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

        case dtd: DoctypeDeclaration =>
          writer.writeDtd(dtd)

        case pi: ProcessingInstruction =>
          writer.writeProcessingInstruction(pi)

        case cdata: CData =>
          writer.writeCData(cdata)

        case entityRef: EntityReference =>
          writer.writeEntityReference(entityRef)

        case null =>
          writer.writeEndElement(toEnd.head)
          toEnd = toEnd.tail
      }
    }
  }
}
