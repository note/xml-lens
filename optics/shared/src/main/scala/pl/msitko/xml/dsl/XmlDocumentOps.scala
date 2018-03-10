package pl.msitko.xml.dsl

import pl.msitko.xml.entities.XmlDocument
import pl.msitko.xml.optics.XmlDocumentOptics

class XmlDocumentOps (doc: XmlDocument) {
  def minimize: XmlDocument = {
    XmlDocumentOptics.rootLens.modify(_.minimize)(doc)
  }
}
