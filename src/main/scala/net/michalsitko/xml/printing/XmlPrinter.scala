package net.michalsitko.xml.printing

import java.io.StringWriter
import javax.xml.stream.{XMLOutputFactory, XMLStreamWriter}

import net.michalsitko.xml.entities._

// TODO: whole XmlPrinter needs rethinking
// current version is very naive. It assumes that input for `print` is basically a result of XmlParser.parse
// which is not true in general. User can manipulate AST in any way, so we should take care of undefined namespaces' prefixes,
// isRepairingNamespaces, escaping special characters
object XmlPrinter {
  def print(elem: LabeledElement): String = {
    val stringWriter = new StringWriter()
    val xmlOutFact = XMLOutputFactory.newInstance()
    val writer = xmlOutFact.createXMLStreamWriter(stringWriter)

    // TODO: assumes XML version 1.0 and utf-8, make it works with different values (change in XmlParser required)
    writer.writeStartDocument("UTF-8", "1.0")
    // it's arbitrary but AFAIK it's the most popular convention
    writer.writeCharacters(System.getProperty("line.separator"))

    loop(elem, writer)

    writer.flush()
    stringWriter.toString
  }

  def loop(node: Node, writer: XMLStreamWriter): Unit = node match {
    case elem: LabeledElement =>
      val default = elem.element.namespaceDeclarations.filter(_.prefix.isEmpty)
      default.headOption.foreach(defaultNs => writer.setDefaultNamespace(defaultNs.uri))

      writeElementLabel(elem.label, writer)
      writeNamespaces(elem.element.namespaceDeclarations, writer)
      writeAttributes(elem.element.attributes, writer)
      elem.element.children.foreach(loop(_, writer))
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
          println(s"bazinga uri: $uri")
          println(s"bazinga localName: ${resolvedName.localName}")
          writer.writeStartElement(uri, resolvedName.localName)
        case None =>
          writer.writeStartElement(resolvedName.localName)
      }
    }

  def writeNamespaces(namespaces: Seq[NamespaceDeclaration], writer: XMLStreamWriter): Unit = {
    namespaces.map { ns =>
      ns.prefix match {
        case Some(prefix) =>
          writer.writeNamespace(prefix, ns.uri)
        case None =>
          println("write DefaultNamespace: " + ns.uri)
          writer.writeDefaultNamespace(ns.uri)
      }
    }
  }

  def writeAttributes(attributes: Seq[Attribute], writer: XMLStreamWriter): Unit = {
    attributes.map { attr =>
      attr.uri match {
        case Some(uri) =>
          writer.writeAttribute(attr.prefix, uri, attr.key, attr.value)
        case None =>
          writer.writeAttribute(attr.key, attr.value)
      }

    }
  }
}
