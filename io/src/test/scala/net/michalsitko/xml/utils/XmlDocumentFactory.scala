package net.michalsitko.xml.utils

import net.michalsitko.xml.entities.{LabeledElement, Prolog, XmlDeclaration, XmlDocument}

object XmlDocumentFactory {
  def noProlog(labeledElement: LabeledElement): XmlDocument =
    XmlDocument(Prolog(None, List.empty, None), labeledElement)

  def withProlog(version: String, encoding: Option[String], root: LabeledElement) =
    XmlDocument(Prolog(Some(XmlDeclaration(version, encoding)), List.empty, None), root)
}
