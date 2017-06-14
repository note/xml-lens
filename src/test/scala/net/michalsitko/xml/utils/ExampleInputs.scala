package net.michalsitko.xml.utils

import net.michalsitko.xml.entities.{LabeledElement, _}
import net.michalsitko.xml.parsing.ParsingException

case class Example(stringRepr: String, expectedRes: Either[ParsingException, LabeledElement])
object Example {
  def right(stringRepr: String, element: LabeledElement) =
    Example(stringRepr, Right(element))
}

trait ExampleInputs extends AnyRef with ExampleBuilderHelper {
  val noNamespaceExample = Example.right(
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a><c1><f>item1</f><g>item2</g></c1><c1><f>item1</f><h>item2</h></c1></a>""".stripMargin,
    labeledElement("a",
      labeledElement("c1",
        labeledElement("f", Text("item1")),
        labeledElement("g", Text("item2"))
      ),
      labeledElement("c1",
        labeledElement("f", Text("item1")),
        labeledElement("h", Text("item2"))
      )
    )
  )

  val noNamespaceXmlStringWithWsExample = Example.right(
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f>item1</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin,
    labeledElement("a",
      indent(1),
      labeledElement("c1",
        indent(2),
        labeledElement("f", Text("item1")),
        indent(2),
        labeledElement("g", Text("item2")),
        indent(1)
      ),
      indent(1),
      labeledElement("c1",
        indent(2),
        labeledElement("f", Text("item1")),
        indent(2),
        labeledElement("h", Text("item2")),
        indent(1)
      ),
      Text(lineBreak)
    )
  )

  val namespaceXmlStringExample = {
    val defaultNs = "http://www.develop.com/student"
    val anotherNs = "http://www.example.com"

    Example.right(
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com">
        |   <c1>
        |      <f>item1</f>
        |      <g>item2</g>
        |   </c1>
        |   <c1>
        |      <f>item1</f>
        |      <xyz:h>item2</xyz:h>
        |   </c1>
        |</a>
      """.stripMargin,
      LabeledElement(ResolvedName("", Some(defaultNs), "a"), Element(Seq.empty, List(
        indent(1),
        LabeledElement(ResolvedName("", Some(defaultNs), "c1"), element(
          indent(2),
          LabeledElement(ResolvedName("", Some(defaultNs), "f"), element(Text("item1"))),
          indent(2),
          LabeledElement(ResolvedName("", Some(defaultNs), "g"), element(Text("item2"))),
          indent(1)
        )),
        indent(1),
        LabeledElement(ResolvedName("", Some(defaultNs), "c1"), element(
          indent(2),
          LabeledElement(ResolvedName("", Some(defaultNs), "f"), element(Text("item1"))),
          indent(2),
          LabeledElement(ResolvedName("xyz", Some(anotherNs), "h"), element(Text("item2"))),
          indent(1)
        )),
        Text(lineBreak)
      ), List(NamespaceDeclaration(None, "http://www.develop.com/student"), NamespaceDeclaration(Some("xyz"), "http://www.example.com"))))
    )
  }

  val attributesXmlStringExample = {
    val fAttributes = List(Attribute.unprefixed("name", "abc"), Attribute.unprefixed("name2", "something else"))
    val c1Attributes = List(Attribute.unprefixed("name", ""))

    Example.right(
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a><c1><f name="abc" name2="something else">item1</f><g>item2</g></c1><c1 name=""><f>item1</f><h>item2</h></c1></a>""".stripMargin,
      labeledElement("a",
        labeledElement("c1",
          LabeledElement(resolvedName("f"), Element(fAttributes, List(Text("item1")), Seq.empty)),
          labeledElement("g", Text("item2"))
        ),
        LabeledElement(resolvedName("c1"), Element(c1Attributes, List(
          labeledElement("f", Text("item1")),
          labeledElement("h", Text("item2"))
        ), Seq.empty))
      )
    )
  }

  val attributesWithNsXmlStringExample = {
    val defaultNs = "http://www.a.com"
    val bNs = "http://www.b.com"

    // https://stackoverflow.com/questions/41561/xml-namespaces-and-attributes
    val fAttributes = List(Attribute.unprefixed("name", "abc"), Attribute(ResolvedName("b", Some(bNs), "attr"), "attr1"))
    val gAttributes = List(Attribute(ResolvedName("b", Some(bNs), "name"), "def"))
    val hAttributes = List(Attribute.unprefixed("name", "ghi"))

    Example.right(
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a xmlns="http://www.a.com" xmlns:b="http://www.b.com"><c1><f name="abc" b:attr="attr1">item1</f><g b:name="def">item2</g><b:h name="ghi">item3</b:h></c1></a>""".stripMargin,
      LabeledElement(ResolvedName("", Some(defaultNs), "a"), Element(Seq.empty, List(
        LabeledElement(ResolvedName("", Some(defaultNs), "c1"), element(
          LabeledElement(ResolvedName("", Some(defaultNs), "f"), Element(fAttributes, List(Text("item1")), Seq.empty)),
          LabeledElement(ResolvedName("", Some(defaultNs), "g"), Element(gAttributes, List(Text("item2")), Seq.empty)),
          LabeledElement(ResolvedName("b", Some(bNs), "h"), Element(hAttributes, List(Text("item3")), Seq.empty))
        ))
      ), List(NamespaceDeclaration(None, "http://www.a.com"), NamespaceDeclaration(Some("b"), "http://www.b.com"))))
    )
  }

  val commentsExamples = List(
    Example.right(
      """<?xml version="1.0" encoding="UTF-8"?><a><!--something --><c1></c1></a>""",
      labeledElement("a",
        Comment("something "),
        labeledElement("c1")
      )
    ),
    Example.right(
      """<?xml version="1.0" encoding="UTF-8"?><a><!--<c0></c0>--><c1></c1></a>""",
      labeledElement("a",
        Comment("<c0></c0>"),
        labeledElement("c1")
      )
    )
  )

  val malformedXmlStrings = List(
    """<?xml version="1.0" encoding="UTF-8"?>
      |a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com">
      |   <c1>
      |      <f>item1</f>
      |   </c1>
      |</a>
      """.stripMargin,
    """<?xml version="1.0" encoding="UTF-8"?>
      |</a>
      |<a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com">
      |   <c1>
      |      <f>item1</f>
      |   </c1>
      |</a>
      """.stripMargin,
    """<?xml version="1.0" encoding="UTF-8"?>
      |</a>
      |<a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com">
      |   <c1>
      |      <yy:f>item1</yy:f>
      |   </c1>
      |</a>
      """.stripMargin,
    // unbound prefix:
    """<?xml version="1.0" encoding="UTF-8"?>
        |<a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com">
        |   <c1>
        |      <yy:f>item1</yy:f>
        |   </c1>
        |</a>
      """.stripMargin,
    """<?xml version="1.0" encoding="UTF-8"?>
        |<a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com">
        |   <c1>
        |      <xyz:f>item1</xyz:f>
        |   </a>
        |</a>
      """.stripMargin,
    "".stripMargin,
    "<></>".stripMargin
  )
}
