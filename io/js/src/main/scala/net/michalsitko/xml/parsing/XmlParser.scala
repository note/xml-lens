package net.michalsitko.xml.parsing

import net.michalsitko.xml.entities.{Attribute, Node, _}

import scala.collection.mutable.ArrayBuffer

object XmlParser {
  def parse(input: String): Either[ParsingException, XmlDocument] = {
    val options = JsParserOptions(xmlns = Some(true))
    val parser = JsParser.apply(strict = true, options = options)

    var root = Option.empty[LabeledElementBuilder]
    var xmlDeclaration = Option.empty[XmlDeclaration]

    var elementStack = List.empty[LabeledElementBuilder]

    // This is hacky. sax-js handles xml declaration as processing instruction even though
    // technically it is not. Also, it does not parse the content of xml declaration at all
    // so for `<?xml version="1.0" encoding="UTF-8"?>` we got JsProcessingInstruction(name="xml", body = "version="1.0" encoding="UTF-8")
    parser.onprocessinginstruction = { pi =>
      if (root.isEmpty && pi.name.toLowerCase == "xml") {
        xmlDeclaration = XmlDeclarationParser.parse(pi.body)
      } else {
        elementStack.headOption.foreach(_.addChild(ProcessingInstruction(pi.name, pi.body)))
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
      println("Bazinga on closetag before")
      val current = elementStack.head
      println("Bazinga on closetag after: " + current.build)
      elementStack.tail.headOption.foreach(_.addChild(current.build))
      elementStack = elementStack.tail
    }

    parser.ontext = { txt =>
      elementStack.headOption.foreach(_.addChild(Text(txt)))
    }

    parser.write(input).close()

    println("xmlparser here!")
    val prolog = Prolog(xmlDeclaration, Seq.empty, None)

    root match {
      case Some(r)  => Right(XmlDocument(prolog, r.build))
      case None     => Left(ParsingException("no root element found", new IllegalArgumentException))
    }

  }

  private def fromNode(node: JsNode): ResolvedName =
    ResolvedName(node.prefix, node.uri, node.local)

  private def fromAttr(attr: JsAttribute): Attribute =
    Attribute(ResolvedName(attr.prefix, attr.uri, attr.local), attr.value)

  private def newElementBuilder(node: JsNode): LabeledElementBuilder = {
    def toNamespaceDeclaration(attr: JsAttribute): NamespaceDeclaration = {
      val prefix = attr.name.split(':').tail.headOption.getOrElse("")
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
