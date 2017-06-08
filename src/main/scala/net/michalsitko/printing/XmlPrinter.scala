package net.michalsitko.printing

import java.io.StringWriter
import javax.xml.stream.{XMLOutputFactory, XMLStreamWriter}

import net.michalsitko.entities._

// TODO: whole XmlPrinter needs rethinking
// current version is very naive. It assumes that input for `print` is basically a result of XmlParser.parse
// which is not true in general. User can manipulate AST in any way, so we should take care of undefined namespaces' prefixes,
// isRepairingNamespaces, escaping special characters
object XmlPrinter {
  def print(elem: Element): String = {
    val stringWriter = new StringWriter()
    val xmlOutFact = XMLOutputFactory.newInstance()
    val writer = xmlOutFact.createXMLStreamWriter(stringWriter)

    // TODO: assumes XML version 1.0 and utf-8, make it works with different values (change in XmlParser required)
    writer.writeStartDocument("UTF-8", "1.0")
    loop(elem, writer)

    writer.flush()
    stringWriter.toString
  }

  def loop(node: Node, writer: XMLStreamWriter): Unit = node match {
    case elem: Element =>
      writeElementLabel(elem.label, writer)
      writeNamespaces(elem.elementDetails.namespaceDeclarations, writer)
      writeAttributes(elem.elementDetails.attributes, writer)
      elem.elementDetails.children.foreach(loop(_, writer))
      writer.writeEndElement()

    case text: Text =>
      writer.writeCharacters(text.text)
  }

  def writeElement(resolvedName: ResolvedName, writer: XMLStreamWriter): Unit = {
  }

  def writeElementLabel(resolvedName: ResolvedName, writer: XMLStreamWriter): Unit =
    if(resolvedName.hasPrefix) { // TODO: probably this if is not needed
      // TODO: get rid of getOrElse(null)
      writer.writeStartElement(resolvedName.prefix, resolvedName.localName, resolvedName.uri.getOrElse(null))
    } else {
      resolvedName.uri match {
        case Some(uri) =>
          writer.writeStartElement(uri, resolvedName.localName)
        case None =>
          writer.writeStartElement(resolvedName.localName)
      }
    }

  def writeNamespaces(namespaces: Seq[NamespaceDeclaration], writer: XMLStreamWriter): Unit = {
    namespaces.map(ns => writer.writeNamespace(ns.prefix.getOrElse(null), ns.uri))
  }

  def writeAttributes(attributes: Seq[Attribute], writer: XMLStreamWriter): Unit = {
    attributes.map(attr => writer.writeAttribute(attr.prefix, attr.uri.getOrElse(null), attr.key, attr.value))
  }
}
