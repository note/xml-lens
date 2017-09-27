package net.michalsitko.xml

import java.io.StringWriter
import javax.xml.stream.XMLOutputFactory

object Main {
  def main(args: Array[String]): Unit = {
    val output = new StringWriter()
    val sw = XMLOutputFactory.newFactory().createXMLStreamWriter(output)

    sw.writeStartDocument("UTF-8", "1.0")
    sw.writeCharacters("\n")
    sw.writeStartElement("a")
    sw.writeCharacters("\n")
    sw.writeCharacters("  ")
    sw.writeStartElement("b")
    sw.writeEmptyElement("c")
    sw.writeAttribute("name", "someName")
    sw.writeEndElement()
    sw.writeCharacters("\n")
    sw.writeEndElement()
    sw.writeEndDocument()

    println(output.toString)
  }
}
