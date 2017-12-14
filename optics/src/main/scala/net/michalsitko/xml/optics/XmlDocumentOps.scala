package net.michalsitko.xml.optics

import net.michalsitko.xml.entities.XmlDocument

class XmlDocumentOps (doc: XmlDocument) {
  def minimize: XmlDocument = {
    import net.michalsitko.xml.syntax.node._
    XmlDocumentOptics.rootLens.modify(_.minimize)(doc)
  }
}
