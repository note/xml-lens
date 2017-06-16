package net.michalsitko.xml.syntax

import net.michalsitko.xml.entities.{LabeledElement, Text}
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.syntax.node._
import net.michalsitko.xml.test.utils.ExampleInputs
import org.scalatest.{Matchers, WordSpec}

class NodeOpsSpec extends WordSpec with Matchers with ExampleInputs {
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

      input.minimize should equal(expectedRes)
    }

    "work as expected 2" in {
      val input = XmlParser.parse(noNamespaceXmlStringWithWsExample.stringRepr).right.get

      val res = input.minimize

      val expectedRes =
        """<?xml version="1.0" encoding="UTF-8"?>
          |<a><c1><f>item1</f><g>item2</g></c1><c1><f>item1</f><h>item2</h></c1></a>""".stripMargin

      // TODO: get rid of asInstanceOf
      XmlPrinter.print(res.asInstanceOf[LabeledElement]) should equal(expectedRes)
    }
  }
}
