package net.michalsitko.parsing.utils

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

}
