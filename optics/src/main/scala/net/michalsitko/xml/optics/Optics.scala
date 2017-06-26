package net.michalsitko.xml.optics

import monocle.{Lens, _}
import net.michalsitko.xml.entities._

import scalaz.Applicative
import scalaz.std.list._
import scalaz.syntax.traverse._

trait LabeledElementOptics {
  def deep(elementMatcher: NameMatcher): Traversal[LabeledElement, Element] =
    element.composeTraversal(ElementOptics.deeper(elementMatcher))

  def deep(label: String): Traversal[LabeledElement, Element] =
    deep(NameMatcher.fromString(label))

  val element = Lens[LabeledElement, Element](_.element){ newElement => from =>
    from.copy(element = newElement)
  }

  val children = element.composeLens(ElementOptics.children)

  val label = Lens[LabeledElement, ResolvedName](_.label)(newLabel => from => from.copy(label = newLabel))

  val localName = label.composeLens(ResolvedNameOptics.localName)
}

object LabeledElementOptics extends LabeledElementOptics

trait ElementOptics {
  def deeper(elementMatcher: NameMatcher): Traversal[Element, Element] = new Traversal[Element, Element] {
    override final def modifyF[F[_]: Applicative](f: (Element) => F[Element])(from: Element): F[Element] = {
      val modified = from.children.collect(modifyOnlyMatching(elementMatcher, f)).toList

      Applicative[F].sequence(modified).map { elements =>
        from.copy(children = elements)
      }
    }
  }

  def deeper(label: String): Traversal[Element, Element] =
    deeper(NameMatcher.fromString(label))

  val hasOneChild: Optional[Element, Node] = Optional[Element, Node]{ el =>
      onlyChild(el)
    }{ newNode => from =>
      onlyChild(from).fold(from)(_ => from.copy(children = Vector(newNode)))
    }

//  def hasChildLabeled(label: String): Prism[Element, Element] =
//    hasChildLabeled(NameMatcher.fromString(label))
//
//  def hasChildLabeled(nameMatcher: NameMatcher): Prism[Element, Element] = Prism.apply[Element, Element]{ el =>
//    childrenElements.find(el => nameMatcher.matches(el.label))(el) match {
//      case Some(_) => Some(el)
//      case None => None
//    }
//  }(identity)

  val hasTextOnly: Optional[Element, String] = hasOneChild.composePrism(NodeOptics.isTextS)

  def attribute(key: NameMatcher) = Optional[Element, String] { el =>
    el.attributes.find(attr => key.matches(attr.key)).map(_.value)
  }{ newValue => from =>
    val newAttributes = from.attributes.collect {
      case attr: Attribute if key.matches(attr.key) =>
        attr.copy(value = newValue)
      case attr =>
        attr
    }

    from.copy(attributes = newAttributes)
  }

  def attribute(key: String): Optional[Element, String] = attribute(NameMatcher.fromString(key))

  val attributes: Lens[Element, Seq[Attribute]] =
    Lens[Element, Seq[Attribute]](_.attributes)(newAttrs => from => from.copy(attributes = newAttrs))

  val namespaces: Lens[Element, Seq[NamespaceDeclaration]] =
    Lens[Element, Seq[NamespaceDeclaration]](_.namespaceDeclarations)(newNs => from => from.copy(namespaceDeclarations = newNs))

  val children = Lens[Element, Seq[Node]](_.children){ newChildren => from =>
    from.copy(children = newChildren)
  }

  val allChildren = new Traversal[Element, Node] {
    def modifyF[F[_]: Applicative](fun: Node => F[Node])(from: Element): F[Element] = {
      from.children.toList.traverse(fun).map { modified =>
        from.copy(children = modified)
      }
    }
  }

  val allLabeledElements: Traversal[Element, LabeledElement] = allChildren.composePrism(NodeOptics.isLabeledElement)

  private def onlyChild(element: Element): Option[Node] = {
    if (element.children.size == 1) {
      Some(element.children.head)
    } else {
      None
    }
  }

  private def modifyOnlyMatching[F[_] : Applicative](elementMatcher: NameMatcher, f: Element => F[Element]): PartialFunction[Node, F[Node]] = {
    case el: LabeledElement if elementMatcher.matches(el.label) =>
      val modifiedElems = f(el.element)
      modifiedElems.map(modifiedElem => LabeledElement(el.label, modifiedElem))
    case anythingElse =>
      Applicative[F].pure(anythingElse)
  }

}

object ElementOptics extends ElementOptics

trait NodeOptics {
  val isText: Prism[Node, Text] = Prism.partial[Node, Text]{
    case text: Text => text
  }(identity)

  val isTextS: PPrism[Node, Node, String, String] = isText.composeIso(TextOptics.textIso)

  val isLabeledElement: Prism[Node, LabeledElement] = Prism.partial[Node, LabeledElement]{
    case elem: LabeledElement => elem
  }(identity)

  val nodeToNodeTraversal = new Traversal[Node, Node] {
    def modifyF[F[_]: Applicative](fun: Node => F[Node])(from: Node): F[Node] = {
      from match {
        case LabeledElement(label, element) =>
          val appOfList = element.children.toList.traverse(fun)
          appOfList.map { ch =>
            LabeledElement(label, element.copy(children = ch))
          }

        case other =>
          Applicative[F].pure(other)
      }
    }
  }

}

object NodeOptics extends NodeOptics

trait TextOptics {
  val textIso: Iso[Text, String] = Iso[Text, String](_.text)(Text(_))
}

object TextOptics extends TextOptics

trait ResolvedNameOptics {
  val localName = Lens[ResolvedName, String](_.localName)(newLocalName => from => from.copy(localName = newLocalName))
}

object ResolvedNameOptics extends ResolvedNameOptics
