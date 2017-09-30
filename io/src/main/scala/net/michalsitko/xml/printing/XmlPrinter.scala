package net.michalsitko.xml.printing

import java.io.StringWriter

import net.michalsitko.xml.entities._

// Printers assumes that javax.xml.stream.isRepairingNamespaces is set to false.
// Instead, they do their own repairing. That's because: 1. Controlling it with (or even relying) on system property
// is inflexible and error prone, 2. Doing it on xml-lens level give better opportunity to be platform
// (and XML writer implementation) independent

// TODO: whole XmlPrinter needs rethinking
// current version is very naive. It assumes that input for `print` is basically a result of XmlParser.parse
// which is not true in general. User can manipulate AST in any way, so we should take care of undefined namespaces' prefixes,
// isRepairingNamespaces, escaping special characters
object XmlPrinter {
  def print(nodes: Seq[Node]): String = {
    val stringOutput = new StringWriter()
    val writer = new JavaXmlWriter(stringOutput)

    // TODO: code duplication
    nodes.foreach {
      case elem: LabeledElement =>
        writeLabeledElement(elem, writer)

      case text: Text =>
        writer.writeText(text)

      case comment: Comment =>
        writer.writeComment(comment)

      case dtd: Dtd =>
        writer.writeDtd(dtd)

      case pi: ProcessingInstruction =>
        writer.writeProcessingInstruction(pi)

      case cdata: CData =>
        writer.writeCData(cdata)
    }

    val res = stringOutput.toString()
    stringOutput.close() // has no effect because of nature of StringWriter, but let's keep it in case of change
    res
  }

//  def print(elem: LabeledElement): String = {
//    val stringOutput = new StringWriter()
//    val writer = new JavaXmlWriter(stringOutput)
//
//    writeLabeledElement(elem, writer)
//
//    val res = stringOutput.toString()
//    stringOutput.close() // has no effect because of nature of StringWriter, but let's keep it in case of change
//    res
//  }

  // TODO: should be removed and print should take config
  def prettyPrint(nodes: Seq[Node], config: PrinterConfig): String = {
    ???
  }

  private def writeLabeledElement(root: LabeledElement, writer: XmlWriter): Unit = {
    var toVisit = List[Node](root)

    while(toVisit.nonEmpty) {
      val current = toVisit.head
      toVisit = toVisit.tail
      current match {
        case elem: LabeledElement =>
          writer.writeLabeled(elem)
          val toAdd = elem.element.children
          toVisit = toAdd.toList ++ (null.asInstanceOf[Node] +: toVisit)

        case text: Text =>
          writer.writeText(text)

        case comment: Comment =>
          writer.writeComment(comment)

        case dtd: Dtd =>
          writer.writeDtd(dtd)

        case pi: ProcessingInstruction =>
          writer.writeProcessingInstruction(pi)

        case cdata: CData =>
          writer.writeCData(cdata)

        case null =>
          writer.writeEndElement()
      }
    }
  }
}