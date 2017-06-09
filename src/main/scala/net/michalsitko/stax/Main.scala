package net.michalsitko.stax

import java.io.{IOException, StringReader}
import javax.xml.stream.{XMLInputFactory, XMLStreamException, XMLStreamReader}
import javax.xml.stream.XMLStreamConstants._

object Main extends AnyRef with Example {
  def main(args: Array[String]): Unit = {
    read(xmlString)
  }

  // TODO:
  // 1. Zaprojektowac drzewo (AST)
  // 2. zrobic testy do parsera
  // 3. napisac parser

  // https://stackoverflow.com/questions/5059224/which-is-the-best-library-for-xml-parsing-in-java
  // https://docs.oracle.com/cd/E13222_01/wls/docs100/xml/stax.html
  private def read(input: String) = {
    try {
      val xmlInFact = XMLInputFactory.newInstance()
      val reader = xmlInFact.createXMLStreamReader(new StringReader(xmlString));
      while(reader.hasNext()) {
        reader.next() match {
          case START_ELEMENT =>
            getName(reader)
            printAttributes(reader)

          case END_ELEMENT =>
            // need to take care of balancing START_ELEMENT and END_ELEMENT
            println("bazinga END_ELEMENT")

          case e =>
            println(s"bazinga other element: $e")
        }
      }
    } catch {
      case e: IOException =>
        println("IOException")
      case e: XMLStreamException =>
        println("XMLStreamException")
    }
  }

  private def getName(reader: XMLStreamReader): Unit = {
    println("getName 0")
    if(reader.hasName()){
      val prefix = reader.getPrefix()
      val uri = reader.getNamespaceURI()
      val localName = reader.getLocalName()

      //TODO: add getNamespaceCount
      println("getName")
      println(s"prefix: $prefix, uri: $uri, localName: $localName")
      println()
    }
  }

  private def printAttributes(reader: XMLStreamReader){
    for (i <- 0 until reader.getAttributeCount) {
      printAttribute(reader, i)
    }
  }
  private def printAttribute(reader: XMLStreamReader, index: Int) {
    val prefix = reader.getAttributePrefix(index)
    val namespace = reader.getAttributeNamespace(index)
    val localName = reader.getAttributeLocalName(index)
    val value = reader.getAttributeValue(index)
    println("printAttribute")
    println(s"prefix: $prefix, namespace: $namespace, localName: $localName, value: $value")
    println()
  }

}

trait Example {
  val xmlString =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a xmlns="http://www.develop.com/student" xmlns:xyz="http://www.example.com">
      |   <c1>
      |      <f>item1</f>
      |   </c1>
      |</a>
    """.stripMargin
}