package net.michalsitko.printing

import net.michalsitko.entities._
import net.michalsitko.parsing.XmlParser
import net.michalsitko.utils.{Example, ExampleInputs}
import org.scalatest.{Matchers, WordSpec}

class XmlPrinterSpec extends WordSpec with Matchers with ExampleInputs {
  "XmlPrinter" should {
    "work for basic example" in {
      check(noNamespaceExample)
    }
  }

  def check(specificExample: Example): Unit = {
    val res = XmlPrinter.print(specificExample.tree)
    res should equal(specificExample.stringRepr)
  }

}
