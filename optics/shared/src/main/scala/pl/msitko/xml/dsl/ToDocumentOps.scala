package pl.msitko.xml.dsl

import pl.msitko.xml.entities.XmlDocument

trait ToDocumentOps {
  implicit def toDocumentOps(xmlDocument: XmlDocument): XmlDocumentOps =
    new XmlDocumentOps(xmlDocument)
}
