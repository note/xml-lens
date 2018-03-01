package pl.msitko.xml.optics

import monocle.Lens
import pl.msitko.xml.entities.{LabeledElement, XmlDocument}

trait XmlDocumentOptics {
  val rootLens = Lens[XmlDocument, LabeledElement](_.root)(newRoot => doc => doc.copy(root = newRoot))
}

object XmlDocumentOptics extends XmlDocumentOptics
