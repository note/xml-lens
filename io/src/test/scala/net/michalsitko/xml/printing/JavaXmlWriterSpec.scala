package net.michalsitko.xml.printing

import java.io.StringWriter

import org.scalatest.{Matchers, WordSpec}

class JavaXmlWriterSpec extends WordSpec with Matchers {
  "should contain invocation" in {

  }

  trait Context {
    val writer = new JavaXmlWriter(new StringWriter())
  }
}
