package pl.msitko.xml.test.utils

import pl.msitko.xml.entities.{LabeledElement, _}

case class Example(stringRepr: String, document: XmlDocument)

object Example {
  val defaultDecl = XmlDeclaration("1.0", Some("UTF-8"))

  def apply(stringRepr: String, root: LabeledElement): Example = {
    val prolog = Prolog(Some(defaultDecl), List.empty, None)
    Example(stringRepr, XmlDocument(prolog, root))
  }

  def withDoctypeDecl(stringRepr: String, doctypeDeclaration: DoctypeDeclaration, root: LabeledElement): Example = {
    val prolog = Prolog(Some(defaultDecl), List.empty, Some((doctypeDeclaration, List.empty)))
    Example(stringRepr, XmlDocument(prolog, root))
  }

  def apply(stringRepr: String, misc: Seq[Misc], root: LabeledElement): Example = {
    val prolog = Prolog(Some(defaultDecl), misc, None)
    Example(stringRepr, XmlDocument(prolog, root))
  }

}

trait ExampleInputs extends AnyRef with ExampleBuilderHelper {
  val noNamespaceExample = Example(
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

    Example(
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com" XMLNS="http://a.com">
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
      LabeledElement(ResolvedName("", defaultNs, "a"),
        Element(
          attributes = List(Attribute(ResolvedName.unprefixed("XMLNS"), "http://a.com")),
          children = List(
            indent(1),
            LabeledElement(ResolvedName("", defaultNs, "c1"), element(
              indent(2),
              LabeledElement(ResolvedName("", defaultNs, "f"), element(Text("item1"))),
              indent(2),
              LabeledElement(ResolvedName("", defaultNs, "g"), element(Text("item2"))),
              indent(1)
            )),
            indent(1),
            LabeledElement(ResolvedName("", defaultNs, "c1"), element(
              indent(2),
              LabeledElement(ResolvedName("", defaultNs, "f"), element(Text("item1"))),
              indent(2),
              LabeledElement(ResolvedName("xyz", anotherNs, "h"), element(Text("item2"))),
              indent(1)
            )),
            Text(lineBreak)
          ),
          namespaceDeclarations =
            List(NamespaceDeclaration("", "http://www.develop.com/student"), NamespaceDeclaration("xyz", "http://www.example.com"))))
    )
  }

  val attributesXmlStringExample = {
    val fAttributes = List(Attribute.unprefixed("name", "abc"), Attribute.unprefixed("name2", "something else"))
    val c1Attributes = List(Attribute.unprefixed("name", ""))

    Example(
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a><c1><f name="abc" name2="something else">item1</f><g>item2</g></c1><c1 name=""><F>item1</F><h>item2</h></c1></a>""".stripMargin,
      labeledElement("a",
        labeledElement("c1",
          LabeledElement.unprefixed("f", Element(fAttributes, List(Text("item1")), Seq.empty)),
          labeledElement("g", Text("item2"))
        ),
        LabeledElement.unprefixed("c1", Element(c1Attributes, List(
          labeledElement("F", Text("item1")),
          labeledElement("h", Text("item2"))
        ), Seq.empty))
      )
    )
  }

  val attributesXmlStringExampleCaseSensitive = {
    val fAttributes = List(Attribute.unprefixed("name", "abc"), Attribute.unprefixed("NaMe", "Hello"), Attribute.unprefixed("name2", "something else"))
    val c1Attributes = List(Attribute.unprefixed("name", ""))

    Example(
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a><c1><f name="abc" "NaMe"="Hello" name2="something else">item1</f><g>item2</g></c1><c1 name=""><f>item1</f><h>item2</h></c1></a>""".stripMargin,
      labeledElement("a",
        labeledElement("c1",
          LabeledElement.unprefixed("f", Element(fAttributes, List(Text("item1")), Seq.empty)),
          labeledElement("g", Text("item2"))
        ),
        LabeledElement.unprefixed("c1", Element(c1Attributes, List(
          labeledElement("f", Text("item1")),
          labeledElement("h", Text("item2"))
        ), Seq.empty))
      )
    )
  }

  val xmlWithDtd = {
    val str = """<?xml version="1.0" encoding="UTF-8"?>
                |<!DOCTYPE html
                |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
                |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
                |[
                |    <!ENTITY test-entity "This <em>is</em> an entity.">
                |]>
                |<note></note>""".stripMargin

    // Note, that there are 2 top level elements: Dtd and LabeledElements, end of line after the Dtd is ignored
    Example.withDoctypeDecl(
      str,
      DoctypeDeclaration("""<!DOCTYPE html
            |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
            |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
            |[
            |    <!ENTITY test-entity "This <em>is</em> an entity.">
            |]>""".stripMargin),
      labeledElement("note")
    )
  }

  val xmlWithDtdIncorrectly = {
    """<?xml version="1.0" encoding="UTF-8"?>
      |<note>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "This <em>is</em> an entity.">
      |]>
      |</note>""".stripMargin
  }

  val xmlWithPI = {
    val str =
      """<?xml version="1.0" encoding="UTF-8"?>
        |<?xml-stylesheet type="text/xsl" href="style.xsl"?>
        |<?welcome  to pg = 10 of tutorials point?>
        |<?welcome?>
        |<note>something<?mso-application progid="Excel.Sheet"?>else</note>
      """.stripMargin

    Example(
      str,
      List(
        ProcessingInstruction("xml-stylesheet", """type="text/xsl" href="style.xsl""""),
        ProcessingInstruction("welcome", "to pg = 10 of tutorials point"),
        ProcessingInstruction("welcome", "")
      ),
      labeledElement("note", Text("something"), ProcessingInstruction("mso-application", """progid="Excel.Sheet""""), Text("else"))
    )
  }

  val   xmlWithCData = {
    val cdataContent = """
                         |    I can use all sorts of reserved characters
                         |    like > < " and &
                         |    or write things like
                         |    <foo></bar>
                         |    but my document is still well formed!
                         |""".stripMargin

    val str =
      s"""<?xml version="1.0" encoding="UTF-8"?>
        |<note>something<![CDATA[$cdataContent]]>else</note>""".stripMargin

    Example(
      str,
      labeledElement("note", Text("something"), CData(cdataContent), Text("else"))
    )
  }

  val attributesWithNsXmlStringExample = {
    val defaultNs = "http://www.a.com"
    val bNs = "http://www.b.com"

    // https://stackoverflow.com/questions/41561/xml-namespaces-and-attributes
    val fAttributes = List(Attribute.unprefixed("name", "abc"), Attribute(ResolvedName("b", bNs, "attr"), "attr1"))
    val gAttributes = List(Attribute(ResolvedName("b", bNs, "name"), "def"))
    val hAttributes = List(Attribute.unprefixed("name", "ghi"))

    Example(
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a xmlns="http://www.a.com" xmlns:b="http://www.b.com"><c1><f name="abc" b:attr="attr1">item1</f><g b:name="def">item2</g><b:h name="ghi">item3</b:h></c1></a>""".stripMargin,
      LabeledElement(ResolvedName("", defaultNs, "a"), Element(Seq.empty, List(
        LabeledElement(ResolvedName("", defaultNs, "c1"), element(
          LabeledElement(ResolvedName("", defaultNs, "f"), Element(fAttributes, List(Text("item1")))),
          LabeledElement(ResolvedName("", defaultNs, "g"), Element(gAttributes, List(Text("item2")))),
          LabeledElement(ResolvedName("b", bNs, "h"), Element(hAttributes, List(Text("item3"))))
        ))
      ), List(NamespaceDeclaration("", "http://www.a.com"), NamespaceDeclaration("b", "http://www.b.com"))))
    )
  }

  val commentsExamples = List(
    Example(
      """<?xml version="1.0" encoding="UTF-8"?><a><!--something --><c1></c1></a>""",
      labeledElement("a",
        Comment("something "),
        labeledElement("c1")
      )
    ),
    Example(
      """<?xml version="1.0" encoding="UTF-8"?><a><!--<c0></c0>--><c1></c1></a>""",
      labeledElement("a",
        Comment("<c0></c0>"),
        labeledElement("c1")
      )
    )
  )

  val emptyStringAsXmlnsValue =
    Example("""<?xml version="1.0" encoding="UTF-8"?>
        |<a xmlns=""></a>
      """.stripMargin,
      LabeledElement.unprefixed("a", Element(namespaceDeclarations = Seq(NamespaceDeclaration("", ""))))
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
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns:b=""></a>
    """.stripMargin,
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns=></a>
    """.stripMargin,
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns></a>
    """.stripMargin,
    "".stripMargin,
    "<></>".stripMargin
  )
}


