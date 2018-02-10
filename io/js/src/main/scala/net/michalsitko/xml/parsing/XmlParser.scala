package net.michalsitko.xml.parsing

import net.michalsitko.xml.entities.{Attribute, Node, _}

import scala.collection.mutable.ArrayBuffer
import scala.util.{Failure, Success, Try}

object XmlParser {
  def parse(input: String): Either[ParsingException, XmlDocument] = {
    val options = JsParserOptions(xmlns = Some(true))

    // TODO: read more about strict, with strict=true test "preserve entities" fails because of parsing error
    val parser = JsParser.apply(strict = true, options = options)

    var root = Option.empty[LabeledElementBuilder]
    var xmlDeclaration = Option.empty[XmlDeclaration]
    var doctypeDeclaration = Option.empty[DoctypeDeclaration]
    var f1 = Vector.empty[Misc]
    var f2 = Vector.empty[Misc]

    var elementStack = List.empty[LabeledElementBuilder]

    val prolog = Try {
      // This is hacky. sax-js handles xml declaration as processing instruction even though
      // technically it is not. Also, it does not parse the content of xml declaration at all
      // so for `<?xml version="1.0" encoding="UTF-8"?>` we got JsProcessingInstruction(name="xml", body = "version="1.0" encoding="UTF-8")
      parser.onprocessinginstruction = { pi =>
        if (root.isEmpty && pi.name.toLowerCase == "xml") {
          XmlDeclarationParser.parse(pi.body) match {
            case Some(decl) =>
              xmlDeclaration = Some(decl)
            case None =>
              throw new ParsingException("Cannot read xml declaration", new RuntimeException)
          }
        } else {
          val processingInstruction = ProcessingInstruction(pi.name, pi.body)
          elementStack.headOption match {
            case Some(parent) =>
              parent.addChild(processingInstruction)
            case None =>
              doctypeDeclaration match {
                case Some(decl) =>
                  f2 = f2 :+ processingInstruction
                case None =>
                  f1 = f1 :+ processingInstruction
              }
          }
        }
      }

      parser.ondoctype = { txt =>
        (root) match {
          case None =>
            val txtStr = s"<!DOCTYPE$txt>"
            doctypeDeclaration = Some(DoctypeDeclaration(txtStr))
          case Some(_) =>
            throw new ParsingException("Inappropriately located doctype declaration", new RuntimeException)
        }
      }

      parser.onopentag = { node =>
        if(elementStack.isEmpty) {
          root = Some(newElementBuilder(node))
          elementStack = root.get :: elementStack
        } else {
          elementStack = newElementBuilder(node) :: elementStack
        }
      }

      parser.onclosetag = { () =>
        val current = elementStack.head
        elementStack.tail.headOption.foreach(_.addChild(current.build))
        elementStack = elementStack.tail
      }

      parser.ontext = { txt =>
        elementStack.headOption.foreach(_.addChild(Text(txt)))
      }

      parser.oncomment = { txt =>
        elementStack.headOption match {
          case Some(parent) => parent.addChild(Comment(txt))
          case None =>
            doctypeDeclaration match {
              case Some(decl) =>
                f2 = f2 :+ Comment(txt)
              case None =>
                f1 = f1 :+ Comment(txt)
            }
        }
      }

      parser.oncdata = { txt =>
        elementStack.headOption.foreach(_.addChild(CData(txt)))
      }

      parser.write(input).close()
      Prolog(xmlDeclaration, f1, doctypeDeclaration.map(v => (v, f2)))
    }

    // TODO: refactor this
    (root, prolog) match {
      case (Some(r), Success(prolog)) =>
        Right(XmlDocument(prolog, r.build))
      case (None, _) =>
        Left(ParsingException("no root element found", new IllegalArgumentException))
      case (_, Failure(e: ParsingException)) =>
        Left(e)
      case (_, Failure(e: scala.scalajs.js.JavaScriptException)) =>
        Left(ParsingException(e.getMessage(), e))
      case (_, Failure(e)) =>
        throw e
    }

  }

  private def fromNode(node: JsNode): ResolvedName =
    ResolvedName(node.prefix, node.uri, node.local)

  private def fromAttr(attr: JsAttribute): Attribute =
    Attribute(ResolvedName(attr.prefix, attr.uri, attr.local), attr.value)

  private def newElementBuilder(node: JsNode): LabeledElementBuilder = {
    def toNamespaceDeclaration(attr: JsAttribute): NamespaceDeclaration = {
      val prefix = attr.name.split(':').tail.headOption.getOrElse("")
      if(prefix.nonEmpty && attr.value.isEmpty) {
        throw new ParsingException(s"namespace declaration value for namespace [$prefix] is empty", new IllegalArgumentException)
      }

      NamespaceDeclaration(prefix, attr.value)
    }

    val name = fromNode(node)

    // we're not using node.ns for namespaces as JsNode.ns does not contain namespace declarations but namespaces
    // available in current scope which is useless for our purposes
    val (nsDeclarations, attrs) = node.attributes.values.partition { attr =>
      // attributes (and xmlns) are case sensitive (that's why we don't `toLowerCase`)
      attr.name == "xmlns" || attr.name.startsWith("xmlns:")
    }

    val actualAttrs = attrs.map(fromAttr).toSeq
    val ns = nsDeclarations.map(toNamespaceDeclaration).toSeq
    new LabeledElementBuilder(name, actualAttrs, ns)
  }
}

private [parsing] class LabeledElementBuilder(label: ResolvedName, attributes: Seq[Attribute], namespaceDeclarations: Seq[NamespaceDeclaration]) {
  val children: ArrayBuffer[Node] = ArrayBuffer.empty[Node]

  def addChild(node: Node): Unit = {
    children += (node)
  }

  def build: LabeledElement = {
    LabeledElement(label, Element(attributes, children.toList, namespaceDeclarations))
  }
}
