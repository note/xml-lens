package net.michalsitko.xml.printing

import net.michalsitko.xml.entities.{Prolog, XmlDocument}

object XmlPrinter {
  def print(doc: XmlDocument)(implicit cfg: PrinterConfig = PrinterConfig.Default): String = {
    val writer: XmlWriter = ???

    println(cfg)

    writer.writeProlog(doc.prolog)
  }
}

trait XmlWriter {
  def writeProlog(prolog: Prolog): String
}
