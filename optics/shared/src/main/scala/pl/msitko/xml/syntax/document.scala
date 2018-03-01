package pl.msitko.xml.syntax

import pl.msitko.xml.entities.XmlDocument
import pl.msitko.xml.optics.XmlDocumentOps

trait ToDocumentOps {
  implicit def toDocumentOps(xmlDocument: XmlDocument): XmlDocumentOps =
    new XmlDocumentOps(xmlDocument)
}

object document extends ToDocumentOps
