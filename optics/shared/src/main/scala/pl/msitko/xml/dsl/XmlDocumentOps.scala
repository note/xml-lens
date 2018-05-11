package pl.msitko.xml.dsl

import pl.msitko.xml.entities.XmlDocument
import pl.msitko.xml.optics.XmlDocumentOptics

final class XmlDocumentOps (val doc: XmlDocument) extends AnyVal {
  def minimize: XmlDocument = {
    XmlDocumentOptics.rootLens.modify(_.minimize)(doc)
  }
}
