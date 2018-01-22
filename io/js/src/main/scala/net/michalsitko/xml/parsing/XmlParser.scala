package net.michalsitko.xml.parsing

import net.michalsitko.xml.entities.{Attribute, Node, _}

import scala.collection.mutable.ArrayBuffer

object XmlParser {
  def parse(input: String): Either[ParsingException, XmlDocument] = {
    val options = JsParserOptions(xmlns = Some(true))
    val parser = JsParser.apply(strict = true, options = options)

    var root: LabeledElementBuilder = null
    var elementStack = List.empty[LabeledElementBuilder]
    parser.onopentag = { node =>
      if(elementStack.isEmpty) {
        root = newElementBuilder(node)
        elementStack = root :: elementStack
      } else {
        elementStack = newElementBuilder(node) :: elementStack
      }
    }

    parser.onclosetag = { () =>
      val current = elementStack.head
      println("Bazinga on closetag: " + current.build)
      elementStack.tail.headOption.foreach(_.addChild(current.build))
      elementStack = elementStack.tail
    }

    parser.ontext = { txt =>
      elementStack.head.addChild(Text(txt))
    }

    parser.write(input).close()

    println("xmlparser here!")
    val prolog = Prolog(None, Seq.empty, None)
    Right(XmlDocument(prolog, root.build))
  }

  private def fromNode(node: JsNode): ResolvedName =
    ResolvedName(node.prefix, node.uri, node.local)

  private def fromAttr(attr: JsAttribute): Attribute =
    Attribute(ResolvedName(attr.prefix, attr.uri, attr.local), attr.value)

  private def newElementBuilder(node: JsNode): LabeledElementBuilder = {
    val name = fromNode(node)
    val attrs = node.attributes.values.map(fromAttr).toSeq
    val ns = node.ns.map(t => NamespaceDeclaration(t._1, t._2)).toSeq
    new LabeledElementBuilder(name, attrs, ns)
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
