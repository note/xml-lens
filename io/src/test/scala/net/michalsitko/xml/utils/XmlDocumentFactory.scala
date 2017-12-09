package net.michalsitko.xml.utils

import net.michalsitko.xml.entities.{LabeledElement, Prolog, XmlDocument}

object XmlDocumentFactory {
  def root(labeledElement: LabeledElement): XmlDocument =
    XmlDocument(Prolog(None, List.empty, None), labeledElement)
}
