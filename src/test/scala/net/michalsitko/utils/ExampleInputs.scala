package net.michalsitko.utils

trait ExampleInputs {
  val noNamespaceXmlString =
    """<?xml version="1.0" encoding="UTF-8"?><a><c1><f>item1</f><g>item2</g></c1><c1><f>item1</f><h>item2</h></c1></a>""".stripMargin

  val noNamespaceXmlStringWithWs =
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
      |</a>""".stripMargin

  val namespaceXmlString =
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
    """.stripMargin

  val attributesXmlString =
    """<?xml version="1.0" encoding="UTF-8"?><a><c1><f name="abc" name2="something else">item1</f><g>item2</g></c1><c1 name = ""><f>item1</f><h>item2</h></c1></a>"""

  val attributesWithNsXmlString =
    """<?xml version="1.0" encoding="UTF-8"?><a xmlns="http://www.a.com" xmlns:b="http://www.b.com"><c1><f name="abc" b:attr="attr1">item1</f><g b:name="def">item2</g><b:h name="ghi">item3</b:h></c1></a>"""

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

}
