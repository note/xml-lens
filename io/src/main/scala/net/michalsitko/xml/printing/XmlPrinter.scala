package net.michalsitko.xml.printing

import net.michalsitko.xml.entities._

sealed trait WriteOperation
case class WriteElement(el: LabeledElement) extends WriteOperation
case object EndElement extends WriteOperation

class MyXmlWriter {
  private [this] val sb = new StringBuilder

  // TODO: assumes XML version 1.0 and utf-8, make it works with different values (change in XmlParser required)
  sb.append("""<?xml version="1.0" encoding="UTF-8"?>""")
  // it's arbitrary but AFAIK it's the most popular convention
  sb.append(System.getProperty("line.separator"))

  def build: String =
    sb.toString

  def writeLabeled(elem: LabeledElement): Unit = {
    writeElementLabel(elem.label)
    writeNamespaces(elem.element.namespaceDeclarations)
    writeAttributes(elem.element.attributes)
    sb.append(">")
  }

  def writeElementLabel(resolvedName: ResolvedName): Unit = {
    sb.append("<")
    sb.append(resolvedNameToString(resolvedName))
  }

  def writeNamespaces(namespaces: Seq[NamespaceDeclaration]): Unit = {
    namespaces.foreach { ns =>
      ns.prefix match {
        case Some(prefix) =>
          sb.append(s""" xmlns:$prefix="${ns.uri}"""")
        case None =>
          sb.append(s""" xmlns="${ns.uri}"""")
      }

    }
  }

  def writeAttributes(attributes: Seq[Attribute]): Unit = {
    attributes.foreach { attr =>
      sb.append(s""" ${resolvedNameToString(attr.key)}="${attr.value}"""")
    }
  }

  def writeText(text: Text): Unit = {
    sb.append(text.text)
  }

  def writeComment(comment: Comment): Unit = {
    sb.append("<!--")
    sb.append(comment.comment)
    sb.append("-->")
  }

  def writeEndElement(label: ResolvedName): Unit = {
    sb.append(s"</${resolvedNameToString(label)}>")
  }

  private def resolvedNameToString(r: ResolvedName): String = {
    if(r.prefix.isEmpty) {
      r.localName
    } else {
      r.prefix + ":" + r.localName
    }
  }
}

// TODO: whole XmlPrinter needs rethinking
// current version is very naive. It assumes that input for `print` is basically a result of XmlParser.parse
// which is not true in general. User can manipulate AST in any way, so we should take care of undefined namespaces' prefixes,
// isRepairingNamespaces, escaping special characters
object XmlPrinter {
  def print(elem: LabeledElement): String = {
    val writer = new MyXmlWriter

    newLoop2(elem, writer)
    writer.build
  }

  def prettyPrint(elem: LabeledElement): String = {
    ???
  }

  def newLoop2(root: LabeledElement, writer: MyXmlWriter) = {
    var toVisit = List[Node](root)
    var toEnd = List.empty[ResolvedName]

    while(toVisit.nonEmpty) {
      val current = toVisit.head
      toVisit = toVisit.tail
      current match {
        case elem: LabeledElement =>
          writer.writeLabeled(elem)
          val toAdd = elem.element.children
          toVisit = toAdd.toList ++ (null.asInstanceOf[Node] +: toVisit)
          toEnd = elem.label :: toEnd

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
