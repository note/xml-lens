package pl.msitko.xml.syntax

trait Examples {
  val inputWithComments =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |   <!--
      |something
      |something more
      |even 	more
      |
      |-
      |-->
      |      <f>item1</f>
      |      <h>item2</h>
      |   </c1>
      |</a>""".stripMargin

  val outputWithComments =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a><c1><f>item1</f><g>item2</g></c1><c1><!--
      |something
      |something more
      |even 	more
      |
      |-
      |--><f>item1</f><h>item2</h></c1></a>""".stripMargin
}
