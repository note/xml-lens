package net.michalsitko.xml.parsing

import net.michalsitko.xml.entities._

object XmlParser {
  def parse(input: String): Either[ParsingException, XmlDocument] = {
    val options = JsParserOptions(xmlns = Some(true))
    val parser = JsParser.apply(strict = true, options = options)


    var res: LabeledElement = null
    parser.onopentag = { node =>
      val name = ResolvedName(node.prefix, node.uri, node.local)
      scalajs.js.special.debugger()
      res = LabeledElement(name, Element())
    }

    println("xmlparser here!")
    val prolog = Prolog(None, Seq.empty, None)
    Right(XmlDocument(prolog, res))
  }
}


