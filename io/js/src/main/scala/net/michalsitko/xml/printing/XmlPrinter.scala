package net.michalsitko.xml.printing

import net.michalsitko.xml.entities._

import scala.scalajs.js
import scala.scalajs.js.JSConverters._

object XmlPrinter {
  // TODO: uncomment:
//  def print(doc: XmlDocument)(implicit cfg: PrinterConfig = PrinterConfig.Default): String = {
//    XmlWriter.write(doc)
//  }

  def print(doc: XmlDocument): String = {
    XmlWriter.write(doc)
  }
}

object XmlWriter {
  def write(doc: XmlDocument): String = {
    // TODO: introduce default config
    val config: XmlBuilderConfig = XmlBuilderConfig(true, "  ", 0, newline = "\n", spacebeforeslash = "")
    XmlBuilder.withWrapper(config) { wrapper =>
      writeProlog(doc.prolog, wrapper)
      writeElement(doc.root, wrapper)
    }
  }

  private def writeProlog(prolog: Prolog, wrapper: JsXmlDocument): Unit = {
    prolog.xmlDeclaration.foreach(toJsDeclaration _ andThen wrapper.dec _)
  }

  private def writeElement(element: Node, wrapper: JsXmlDocument): Unit = element match {
    case element: LabeledElement =>
      wrapper.ele(resolve(element.label), element.element.attributes.map(toJsAttribute).toMap.toJSDictionary)
      element.element.children.foreach(writeElement(_, wrapper))
      wrapper.up()
    case Text(txt) =>
      wrapper.txt(txt)
    case ProcessingInstruction(target, data) =>
      wrapper.ins(target, data)
    case CData(txt) =>
      wrapper.dat(txt)
    case EntityReference(name: String, replacement: String) =>
      wrapper.entity(name, replacement)
    case Comment(comment) =>
      wrapper.com(comment)
  }

  private def toJsDeclaration(xmlDeclaration: XmlDeclaration): js.Dictionary[String] =
    (Map("version" -> xmlDeclaration.version) ++
      xmlDeclaration.encoding.fold(Map.empty[String, String])(v => Map("encoding" -> v))
      ).toJSDictionary

  private def toJsAttribute(attribute: Attribute): (String, String) =
    (resolve(attribute.key) -> attribute.value)

  private def resolve(name: ResolvedName): String =
    if (name.hasPrefix) {
      name.prefix + ":" + name.localName
    } else {
      name.localName
    }
}
