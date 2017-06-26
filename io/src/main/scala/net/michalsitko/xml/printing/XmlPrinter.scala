package net.michalsitko.xml.printing

import net.michalsitko.xml.entities._

sealed trait WriteOperation
case class WriteElement(el: LabeledElement) extends WriteOperation
case object EndElement extends WriteOperation

trait Writer {
  def build: String

  def writeLabeled(elem: LabeledElement): Unit

  def writeText(text: Text): Unit

  def writeComment(comment: Comment): Unit

  def writeEndElement(elem: LabeledElement): Unit
}

class PrettyXmlWriter (cfg: PrinterConfig) extends Writer {
  private [this] val sb = new StringBuilder

  private var nestedLevel: Int = 0

  private val EOL = System.getProperty("line.separator")

  // TODO: assumes XML version 1.0 and utf-8, make it works with different values (change in XmlParser required)
  sb.append("""<?xml version="1.0" encoding="UTF-8"?>""")

  def build: String =
    sb.toString

  val ident: Int => Unit = cfg.identWith match {
    case Some(identWith) => identLevel =>
      sb.append(EOL)
      for (i <- 0 until identLevel) {
        sb.append(identWith)
      }
    case None =>
      _ => ()
  }

  def writeLabeled(elem: LabeledElement): Unit = {
    ident(nestedLevel)
    writeElementLabel(elem.label)
    writeNamespaces(elem.element.namespaceDeclarations)
    writeAttributes(elem.element.attributes)
    sb.append(">")
    nestedLevel += 1
  }

  private def writeElementLabel(resolvedName: ResolvedName): Unit = {
    sb.append("<")
    sb.append(resolvedNameToString(resolvedName))
  }

  private def writeNamespaces(namespaces: Seq[NamespaceDeclaration]): Unit = {
    namespaces.foreach { ns =>
      ns.prefix match {
        case Some(prefix) =>
          sb.append(s""" xmlns:$prefix="${ns.uri}"""")
        case None =>
          sb.append(s""" xmlns="${ns.uri}"""")
      }

    }
  }

  private def writeAttributes(attributes: Seq[Attribute]): Unit = {
    attributes.foreach { attr =>
      sb.append(s""" ${resolvedNameToString(attr.key)}="${attr.value}"""")
    }
  }

  def writeText(text: Text): Unit = {
    if(cfg.identWith.isDefined && text.text.forall(_.isWhitespace)) {

    } else {
      sb.append(text.text)
    }
  }

  def writeComment(comment: Comment): Unit = {
    sb.append("<!--")
    sb.append(comment.comment)
    sb.append("-->")
  }

  def writeEndElement(elem: LabeledElement): Unit = {
    val hasChildren = elem.element.children.exists {
      case el: LabeledElement =>
        true
      case _ =>
        false
    }
    nestedLevel -= 1
    if(hasChildren) {
      ident(nestedLevel)
    }
    sb.append(s"</${resolvedNameToString(elem.label)}>")
  }

  private def resolvedNameToString(r: ResolvedName): String = {
    if(r.prefix.isEmpty) {
      r.localName
    } else {
      r.prefix + ":" + r.localName
    }
  }

}

class MyXmlWriter extends Writer {
  private [this] val sb = new StringBuilder

  private val EOL = System.getProperty("line.separator")

  // TODO: assumes XML version 1.0 and utf-8, make it works with different values (change in XmlParser required)
  sb.append("""<?xml version="1.0" encoding="UTF-8"?>""")
  sb.append(EOL)

  def build: String =
    sb.toString

  def writeLabeled(elem: LabeledElement): Unit = {
    writeElementLabel(elem.label)
    writeNamespaces(elem.element.namespaceDeclarations)
    writeAttributes(elem.element.attributes)
    sb.append(">")
  }

  private def writeElementLabel(resolvedName: ResolvedName): Unit = {
    sb.append("<")
    sb.append(resolvedNameToString(resolvedName))
  }

  private def writeNamespaces(namespaces: Seq[NamespaceDeclaration]): Unit = {
    namespaces.foreach { ns =>
      ns.prefix match {
        case Some(prefix) =>
          sb.append(s""" xmlns:$prefix="${ns.uri}"""")
        case None =>
          sb.append(s""" xmlns="${ns.uri}"""")
      }

    }
  }

  private def writeAttributes(attributes: Seq[Attribute]): Unit = {
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

  def writeEndElement(elem: LabeledElement): Unit = {
    sb.append(s"</${resolvedNameToString(elem.label)}>")
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

  def prettyPrint(elem: LabeledElement, config: PrinterConfig): String = {
    val writer = new PrettyXmlWriter(config)
    newLoop2(elem, writer)
    writer.build
  }

  def newLoop2(root: LabeledElement, writer: Writer) = {
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

        case null =>
          writer.writeEndElement(toEnd.head)
          toEnd = toEnd.tail
      }
    }
  }
}

case class PrinterConfig(identWith: Option[String])
