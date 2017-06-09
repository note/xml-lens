package net.michalsitko.utils

import net.michalsitko.xml.entities._

case class Example(stringRepr: String, tree: LabeledElement)

trait ExampleInputs {
  val noNamespaceExample = Example(
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a><c1><f>item1</f><g>item2</g></c1><c1><f>item1</f><h>item2</h></c1></a>""".stripMargin,
    LabeledElement(resolvedName("a"), element(List(
      LabeledElement(resolvedName("c1"), element(List(
        LabeledElement(resolvedName("f"), element(List(Text("item1")))),
        LabeledElement(resolvedName("g"), element(List(Text("item2"))))
      ))),
      LabeledElement(resolvedName("c1"), element(List(
        LabeledElement(resolvedName("f"), element(List(Text("item1")))),
        LabeledElement(resolvedName("h"), element(List(Text("item2"))))
      )))
    )))
  )

  val noNamespaceXmlStringWithWsExample = Example(
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
    LabeledElement(resolvedName("a"), element(List(
      indent(1),
      LabeledElement(resolvedName("c1"), element(List(
        indent(2),
        LabeledElement(resolvedName("f"), element(List(Text("item1")))),
        indent(2),
        LabeledElement(resolvedName("g"), element(List(Text("item2")))),
        indent(1)
      ))),
      indent(1),
      LabeledElement(resolvedName("c1"), element(List(
        indent(2),
        LabeledElement(resolvedName("f"), element(List(Text("item1")))),
        indent(2),
        LabeledElement(resolvedName("h"), element(List(Text("item2")))),
        indent(1)
      ))),
      Text(lineBreak)
    )))
  )

  val namespaceXmlStringExample = {
    val defaultNs = "http://www.develop.com/student"
    val anotherNs = "http://www.example.com"

    Example(
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
        LabeledElement(ResolvedName("", Some(defaultNs), "c1"), element(List(
          indent(2),
          LabeledElement(ResolvedName("", Some(defaultNs), "f"), element(List(Text("item1")))),
          indent(2),
          LabeledElement(ResolvedName("", Some(defaultNs), "g"), element(List(Text("item2")))),
          indent(1)
        ))),
        indent(1),
        LabeledElement(ResolvedName("", Some(defaultNs), "c1"), element(List(
          indent(2),
          LabeledElement(ResolvedName("", Some(defaultNs), "f"), element(List(Text("item1")))),
          indent(2),
          LabeledElement(ResolvedName("xyz", Some(anotherNs), "h"), element(List(Text("item2")))),
          indent(1)
        ))),
        Text(lineBreak)
      ), List(NamespaceDeclaration(None, "http://www.develop.com/student"), NamespaceDeclaration(Some("xyz"), "http://www.example.com"))))
    )
  }

  val attributesXmlStringExample = {
    val fAttributes = List(Attribute("", None, "name", "abc"), Attribute("", None, "name2", "something else"))
    val c1Attributes = List(Attribute("", None, "name", ""))

    Example(
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a><c1><f name="abc" name2="something else">item1</f><g>item2</g></c1><c1 name=""><f>item1</f><h>item2</h></c1></a>""".stripMargin,
      LabeledElement(resolvedName("a"), element(List(
        LabeledElement(resolvedName("c1"), element(List(
          LabeledElement(resolvedName("f"), Element(fAttributes, List(Text("item1")), Seq.empty)),
          LabeledElement(resolvedName("g"), element(List(Text("item2"))))
        ))),
        LabeledElement(resolvedName("c1"), Element(c1Attributes, List(
          LabeledElement(resolvedName("f"), element(List(Text("item1")))),
          LabeledElement(resolvedName("h"), element(List(Text("item2"))))
        ), Seq.empty))
      )))
    )
  }

  val attributesWithNsXmlStringExample = {
    val defaultNs = "http://www.a.com"
    val bNs = "http://www.b.com"

    // https://stackoverflow.com/questions/41561/xml-namespaces-and-attributes
    val fAttributes = List(Attribute("", None, "name", "abc"), Attribute("b", Some(bNs), "attr", "attr1"))
    val gAttributes = List(Attribute("b", Some(bNs), "name", "def"))
    val hAttributes = List(Attribute("", None, "name", "ghi"))

    Example(
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a xmlns="http://www.a.com" xmlns:b="http://www.b.com"><c1><f name="abc" b:attr="attr1">item1</f><g b:name="def">item2</g><b:h name="ghi">item3</b:h></c1></a>""".stripMargin,
      LabeledElement(ResolvedName("", Some(defaultNs), "a"), Element(Seq.empty, List(
        LabeledElement(ResolvedName("", Some(defaultNs), "c1"), element(List(
          LabeledElement(ResolvedName("", Some(defaultNs), "f"), Element(fAttributes, List(Text("item1")), Seq.empty)),
          LabeledElement(ResolvedName("", Some(defaultNs), "g"), Element(gAttributes, List(Text("item2")), Seq.empty)),
          LabeledElement(ResolvedName("b", Some(bNs), "h"), Element(hAttributes, List(Text("item3")), Seq.empty))
        )))
      ), List(NamespaceDeclaration(None, "http://www.a.com"), NamespaceDeclaration(Some("b"), "http://www.b.com"))))
    )
  }

  val malformedXmlString =
    """<?xml version="1.0" encoding="UTF-8"?>
      |a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com">
      |   <c1>
      |      <f>item1</f>
      |   </c1>
      |</a>
    """.stripMargin

  val malformedXmlString2 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |</a>
      |<a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com">
      |   <c1>
      |      <f>item1</f>
      |   </c1>
      |</a>
    """.stripMargin

  val malformedNamespaces =
    """<?xml version="1.0" encoding="UTF-8"?>
      |</a>
      |<a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com">
      |   <c1>
      |      <yy:f>item1</yy:f>
      |   </c1>
      |</a>
    """.stripMargin


  lazy val lineBreak = System.getProperty("line.separator")
  lazy val indent = " " * 3
  lazy val lineBreakWithIndent = s"$lineBreak$indent"

  def element(children: Seq[Node]): Element = {
    Element(Seq.empty, children, Seq.empty)
  }

  def indent(level: Int): Text = Text(lineBreak + (indent * level))

  def resolvedName(name: String) = ResolvedName("", None, name)
}
