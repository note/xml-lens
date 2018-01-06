package net.michalsitko.xml.optics

import monocle.Lens
import net.michalsitko.xml.entities.{LabeledElement, XmlDocument}

trait XmlDocumentOptics {
  val rootLens = Lens[XmlDocument, LabeledElement](_.root)(newRoot => doc => doc.copy(root = newRoot))
}

object XmlDocumentOptics extends XmlDocumentOptics
