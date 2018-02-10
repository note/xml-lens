package net.michalsitko.xml.utils

import net.michalsitko.xml.entities.{LabeledElement, Prolog, XmlDeclaration, XmlDocument}

// TODO: maybe it should be moved to AST (or some other non-test module). It may be useful for users creating XML documents from scratch
object XmlDocumentFactory {
  def noProlog(labeledElement: LabeledElement): XmlDocument =
    XmlDocument(Prolog(None, List.empty, None), labeledElement)

  def withProlog(version: String, encoding: Option[String], root: LabeledElement) =
    XmlDocument(Prolog(Some(XmlDeclaration(version, encoding)), List.empty, None), root)
}
