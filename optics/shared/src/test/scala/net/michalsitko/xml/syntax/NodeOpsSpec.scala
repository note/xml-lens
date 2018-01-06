package net.michalsitko.xml.syntax

import net.michalsitko.xml.BasicSpec
import net.michalsitko.xml.entities.Text
import net.michalsitko.xml.printing.PrinterConfig
import net.michalsitko.xml.syntax.document._
import net.michalsitko.xml.syntax.node._
import net.michalsitko.xml.test.utils.ExampleInputs

trait NodeOpsSpec extends BasicSpec with ExampleInputs with Examples {
  implicit val printerConfig = PrinterConfig.Default

  "minimize" should {
    "work as expected" in {
      val input = labeledElement("a",
        Text("hello"),
        labeledElement("b",
          Text("world"),
          Text("")
        ),
        labeledElement("c",
          labeledElement("d",
            Text("\n\n")
          )
        )
      )

      val expectedRes = labeledElement("a",
        Text("hello"),
        labeledElement("b",
          Text("world")
        ),
        labeledElement("c",
          labeledElement("d")
        )
      )

      input.minimize should === (expectedRes)
    }

    "work as expected 2" in {
      val doc = parseExample(noNamespaceXmlStringWithWsExample).minimize

      val expectedRes =
        """<?xml version="1.0" encoding="UTF-8"?>
          |<a><c1><f>item1</f><g>item2</g></c1><c1><f>item1</f><h>item2</h></c1></a>""".stripMargin

      print(doc) should === (expectedRes)
    }

    "respect comments" in {
      val doc = parse(inputWithComments).minimize

      print(doc) should === (outputWithComments)
    }
  }
}

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
