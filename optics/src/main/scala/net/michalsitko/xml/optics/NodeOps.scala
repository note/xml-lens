package net.michalsitko.xml.optics

import monocle.function.Plated
import net.michalsitko.xml.entities.{LabeledElement, Node, Text}

final class NodeOps (root: LabeledElement) {
  import OpticsInstances._

  def minimize: LabeledElement = {
    val withDeepChanges = minimizeTransformation(root).asInstanceOf[LabeledElement]

    // minimizeTransformation will do all the job except of modifying root element itself
    // have not investigated deeply but seems that's how Plated is supposed to work
    // therefore we need following line:
    removeNonSignificantTexts(withDeepChanges)
  }

  private val minimizeTransformation = Plated.transform[Node]{
    case element: LabeledElement =>
      removeNonSignificantTexts(element)
    case n => n
  }_

  private val removeNonSignificantTexts =
    LabeledElementOptics.children.modify(_.filterNot(nonSignificantText))

  private def nonSignificantText(someNode: Node): Boolean = someNode match {
    case Text(text) => text.forall(_.isWhitespace)
    case _ => false
  }
}

