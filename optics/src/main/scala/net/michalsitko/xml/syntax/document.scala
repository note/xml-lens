package net.michalsitko.xml.syntax

import net.michalsitko.xml.entities.XmlDocument
import net.michalsitko.xml.optics.XmlDocumentOps

trait ToDocumentOps {
  implicit def toDocumentOps(xmlDocument: XmlDocument): XmlDocumentOps =
    new XmlDocumentOps(xmlDocument)
}

object document extends ToDocumentOps
