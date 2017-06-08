package net.michalsitko.utils

import net.michalsitko.entities._

case class Example(stringRepr: String, tree: Element)

trait ExampleInputs {
  val noNamespaceExample = Example(
    """<?xml version="1.0" encoding="UTF-8"?><a><c1><f>item1</f><g>item2</g></c1><c1><f>item1</f><h>item2</h></c1></a>""",
    Element(resolvedName("a"), details(List(
      Element(resolvedName("c1"), details(List(
        Element(resolvedName("f"), details(List(Text("item1")))),
        Element(resolvedName("g"), details(List(Text("item2"))))
      ))),
      Element(resolvedName("c1"), details(List(
        Element(resolvedName("f"), details(List(Text("item1")))),
        Element(resolvedName("h"), details(List(Text("item2"))))
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
    Element(resolvedName("a"), details(List(
      indent(1),
      Element(resolvedName("c1"), details(List(
        indent(2),
        Element(resolvedName("f"), details(List(Text("item1")))),
        indent(2),
        Element(resolvedName("g"), details(List(Text("item2")))),
        indent(1)
      ))),
      indent(1),
      Element(resolvedName("c1"), details(List(
        indent(2),
        Element(resolvedName("f"), details(List(Text("item1")))),
        indent(2),
        Element(resolvedName("h"), details(List(Text("item2")))),
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
      Element(ResolvedName("", Some(defaultNs), "a"), Details(Seq.empty, List(
        indent(1),
        Element(ResolvedName("", Some(defaultNs), "c1"), details(List(
          indent(2),
          Element(ResolvedName("", Some(defaultNs), "f"), details(List(Text("item1")))),
          indent(2),
          Element(ResolvedName("", Some(defaultNs), "g"), details(List(Text("item2")))),
          indent(1)
        ))),
        indent(1),
        Element(ResolvedName("", Some(defaultNs), "c1"), details(List(
          indent(2),
          Element(ResolvedName("", Some(defaultNs), "f"), details(List(Text("item1")))),
          indent(2),
          Element(ResolvedName("xyz", Some(anotherNs), "h"), details(List(Text("item2")))),
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
      """<?xml version="1.0" encoding="UTF-8"?><a><c1><f name="abc" name2="something else">item1</f><g>item2</g></c1><c1 name = ""><f>item1</f><h>item2</h></c1></a>""",
      Element(resolvedName("a"), details(List(
        Element(resolvedName("c1"), details(List(
          Element(resolvedName("f"), Details(fAttributes, List(Text("item1")), Seq.empty)),
          Element(resolvedName("g"), details(List(Text("item2"))))
        ))),
        Element(resolvedName("c1"), Details(c1Attributes, List(
          Element(resolvedName("f"), details(List(Text("item1")))),
          Element(resolvedName("h"), details(List(Text("item2"))))
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
      """<?xml version="1.0" encoding="UTF-8"?><a xmlns="http://www.a.com" xmlns:b="http://www.b.com"><c1><f name="abc" b:attr="attr1">item1</f><g b:name="def">item2</g><b:h name="ghi">item3</b:h></c1></a>""",
      Element(ResolvedName("", Some(defaultNs), "a"), Details(Seq.empty, List(
        Element(ResolvedName("", Some(defaultNs), "c1"), details(List(
          Element(ResolvedName("", Some(defaultNs), "f"), Details(fAttributes, List(Text("item1")), Seq.empty)),
          Element(ResolvedName("", Some(defaultNs), "g"), Details(gAttributes, List(Text("item2")), Seq.empty)),
          Element(ResolvedName("b", Some(bNs), "h"), Details(hAttributes, List(Text("item3")), Seq.empty))
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

  def details(children: Seq[Node]): Details = {
    Details(Seq.empty, children, Seq.empty)
  }

  def indent(level: Int): Text = Text(lineBreak + (indent * level))

  def resolvedName(name: String) = ResolvedName("", None, name)
}
