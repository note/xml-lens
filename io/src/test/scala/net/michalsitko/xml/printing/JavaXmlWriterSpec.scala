package net.michalsitko.xml.printing

import java.io.StringWriter

import org.scalatest.{Matchers, WordSpec}

// TODO: is it even needed?
class JavaXmlWriterSpec extends WordSpec with Matchers {
  val printerConfig = XmlPrinter.DefaultPrinterConfig

  "should contain invocation" in {

  }

  trait Context {
    val writer = new JavaXmlWriter(new StringWriter(), printerConfig)
  }
}
