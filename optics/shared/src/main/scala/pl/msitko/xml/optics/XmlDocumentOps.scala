package pl.msitko.xml.optics

import pl.msitko.xml.entities.XmlDocument

class XmlDocumentOps (doc: XmlDocument) {
  def minimize: XmlDocument = {
    import pl.msitko.xml.syntax.node._
    XmlDocumentOptics.rootLens.modify(_.minimize)(doc)
  }
}
