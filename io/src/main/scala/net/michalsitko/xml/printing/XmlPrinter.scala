package net.michalsitko.xml.printing

import java.io.StringWriter
import javax.xml.stream.{XMLOutputFactory, XMLStreamWriter}

import net.michalsitko.xml.entities._

import scala.annotation.tailrec
import scala.collection.mutable.Buffer

sealed trait WriteOperation
case class WriteElement(el: LabeledElement) extends WriteOperation
case object EndElement extends WriteOperation

// TODO: whole XmlPrinter needs rethinking
// current version is very naive. It assumes that input for `print` is basically a result of XmlParser.parse
// which is not true in general. User can manipulate AST in any way, so we should take care of undefined namespaces' prefixes,
// isRepairingNamespaces, escaping special characters
object XmlPrinter {
  def print(elem: LabeledElement): String = {
    val sb = new StringBuilder

    // TODO: assumes XML version 1.0 and utf-8, make it works with different values (change in XmlParser required)
    sb.append("""<?xml version="1.0" encoding="UTF-8"?>""")
    // it's arbitrary but AFAIK it's the most popular convention
    sb.append(System.getProperty("line.separator"))

    newLoop2(elem, sb)
    sb.toString
  }

  def prettyPrint(elem: LabeledElement): String = {
    val stringWriter = new StringWriter()
    val xmlOutFact = XMLOutputFactory.newInstance()
    val writer = xmlOutFact.createXMLStreamWriter(stringWriter)

    // TODO: assumes XML version 1.0 and utf-8, make it works with different values (change in XmlParser required)
    writer.writeStartDocument("UTF-8", "1.0")
    // it's arbitrary but AFAIK it's the most popular convention
    writer.writeCharacters(System.getProperty("line.separator"))

    newLoop(elem, writer, List.empty[Vector[Node]])

    writer.flush()
    stringWriter.toString
  }

  def newLoop2(root: LabeledElement, sb: StringBuilder) = {

    var toVisit = Buffer[Either[String, Node]](Right(root))

    while(toVisit.nonEmpty) {
      val current = toVisit.head
      toVisit = toVisit.tail
      current match {
        case Right(elem: LabeledElement) =>
          writeLabeled2(elem, sb)
          val toAdd = elem.element.children.map(Right(_).asInstanceOf[Either[String, Node]]).toBuffer
          toAdd.append(Left(resolvedNameToString(elem.label)))
          toVisit = toAdd ++ toVisit

        case Right(text: Text) =>
          sb.append(text.text)

        case Right(comment: Comment) =>
          sb.append("<!--")
          sb.append(comment.comment)
          sb.append("-->")

        case Left(str) =>
          sb.append(s"</$str>")
      }
    }
  }

  private def resolvedNameToString(r: ResolvedName): String = {
    if(r.prefix.isEmpty) {
      r.localName
    } else {
      r.prefix + ":" + r.localName
    }
  }

  @tailrec
  def newLoop(node: Node, writer: XMLStreamWriter, acc: List[Vector[Node]]): Unit = node match {
    case elem: LabeledElement =>
      writeLabeled(elem, writer)

      if (elem.element.children.isEmpty) {
        writer.writeEndElement()

        val empty = acc.takeWhile(_.isEmpty)
        empty.foreach(_ => writer.writeEndElement())
        val newAcc = acc.drop(empty.size)

        newAcc match {
          case firstNodes :: otherNodes =>
            // we can call head because we already filtered out empty Vectors
            newLoop(firstNodes.head, writer, firstNodes.tail :: newAcc.tail)
          case Nil =>
            ()
        }
      } else {
        // TODO: investigate toVector - element.children are of type Seq[Node] - is there a way to ensure they're vector
        // without changing interface?
        newLoop(elem.element.children.head, writer, elem.element.children.tail.toVector :: acc)
      }

    case text: Text =>
      writer.writeCharacters(text.text)

      val empty = acc.takeWhile(_.isEmpty)
      empty.foreach(_ => writer.writeEndElement())
      val newAcc = acc.drop(empty.size)

      newAcc match {
        case firstNodes :: otherNodes =>
          // we can call head because we already filtered out empty Vectors
          newLoop(firstNodes.head, writer, firstNodes.tail :: newAcc.tail)
        case Nil =>
          ()
      }

    case comment: Comment =>
      writer.writeComment(comment.comment)

      val empty = acc.takeWhile(_.isEmpty)
      empty.foreach(_ => writer.writeEndElement())
      val newAcc = acc.drop(empty.size)

      newAcc match {
        case firstNodes :: otherNodes =>
          // we can call head because we already filtered out empty Vectors
          newLoop(firstNodes.head, writer, firstNodes.tail :: newAcc.tail)
        case Nil =>
          ()
      }
  }

  def writeLabeled(elem: LabeledElement, writer: XMLStreamWriter): Unit = {
    val default = elem.element.namespaceDeclarations.filter(_.prefix.isEmpty)
    default.headOption.foreach(defaultNs => writer.setDefaultNamespace(defaultNs.uri))

    writeElementLabel(elem.label, writer)
    writeNamespaces(elem.element.namespaceDeclarations, writer)
    writeAttributes(elem.element.attributes, writer)
  }

  def writeLabeled2(elem: LabeledElement, sb: StringBuilder): Unit = {
    writeElementLabel2(elem.label, sb)
    writeNamespaces2(elem.element.namespaceDeclarations, sb)
    writeAttributes2(elem.element.attributes, sb)
    sb.append(">")
  }

  def writeElementLabel2(resolvedName: ResolvedName, sb: StringBuilder): Unit = {
    sb.append("<")
    sb.append(resolvedNameToString(resolvedName))
  }

  def writeNamespaces2(namespaces: Seq[NamespaceDeclaration], sb: StringBuilder): Unit = {
    namespaces.foreach { ns =>
      ns.prefix match {
        case Some(prefix) =>
          sb.append(s""" xmlns:$prefix="${ns.uri}"""")
        case None =>
          sb.append(s""" xmlns="${ns.uri}"""")
      }

    }
  }

  def writeAttributes2(attributes: Seq[Attribute], sb: StringBuilder): Unit = {
    attributes.foreach { attr =>
      sb.append(s""" ${resolvedNameToString(attr.key)}="${attr.value}"""")
    }
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
    namespaces.map { ns =>
      ns.prefix match {
        case Some(prefix) =>
          writer.writeNamespace(prefix, ns.uri)
        case None =>
          writer.writeDefaultNamespace(ns.uri)
      }
    }
  }

  def writeAttributes(attributes: Seq[Attribute], writer: XMLStreamWriter): Unit = {
    attributes.map { attr =>
      attr.key.uri match {
        case Some(uri) =>
          writer.writeAttribute(attr.key.prefix, uri, attr.key.localName, attr.value)
        case None =>
          writer.writeAttribute(attr.key.localName, attr.value)
      }

    }
  }
}
