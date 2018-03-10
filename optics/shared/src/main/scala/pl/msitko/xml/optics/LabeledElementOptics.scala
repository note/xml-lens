package pl.msitko.xml.optics

import monocle.function.Index
import monocle.{Lens, Optional, Traversal}
import pl.msitko.xml.entities._

import scalaz.Applicative

trait LabeledElementOptics {
  def deep(elementMatcher: NameMatcher): Traversal[LabeledElement, Element] =
    element.composeTraversal(ElementOptics.deeper(elementMatcher))

  def deep(label: String): Traversal[LabeledElement, Element] =
    deep(NameMatcher.fromString(label))

  def isLabeled(elementMatcher: NameMatcher): Optional[LabeledElement, Element] =
    Optional[LabeledElement, Element]{ labeled =>
      if (elementMatcher.matches(labeled.label)) {
        Some(labeled.element)
      } else {
        None
      }
    }{ newElem => labeled =>
      if(elementMatcher.matches(labeled.label)) {
        labeled.copy(element = newElem)
      } else {
        labeled
      }
    }

  def isLabeled(label: String): Optional[LabeledElement, Element] =
    isLabeled(NameMatcher.fromString(label))

  val element: Lens[LabeledElement, Element] = Lens[LabeledElement, Element](_.element){ newElement =>from =>
    from.copy(element = newElement)
  }

  val children = element.composeLens(ElementOptics.children)

  val label: Lens[LabeledElement, ResolvedName] =
    Lens[LabeledElement, ResolvedName](_.label)(newLabel => from => from.copy(label = newLabel))

  val localName = label.composeLens(ResolvedNameOptics.localName)

  val index: Index[LabeledElement, Int, Node] = new Index[LabeledElement, Int, Node] {
    override def index(i: Int): Optional[LabeledElement, Node] =
      element.composeOptional(ElementOptics.indexOptional(i))
  }

  val elementIndex: Index[LabeledElement, Int, LabeledElement] = new Index[LabeledElement, Int, LabeledElement] {
    override def index(i: Int): Optional[LabeledElement, LabeledElement] =
      element.composeOptional(ElementOptics.indexElementOptional(i))
  }

  val allLabeledElements: Traversal[LabeledElement, LabeledElement] = element.composeTraversal(ElementOptics.allLabeledElements)
  val allTexts: Traversal[LabeledElement, Text] = element.composeTraversal(ElementOptics.allTexts)

  val labeledElementTraversal = new Traversal[LabeledElement, LabeledElement] {
    def modifyF[F[_]: Applicative](fun: LabeledElement => F[LabeledElement])(from: LabeledElement): F[LabeledElement] = {
      element.composeTraversal(ElementOptics.allLabeledElements).modifyF(fun)(from)
    }
  }
}

object LabeledElementOptics extends LabeledElementOptics
