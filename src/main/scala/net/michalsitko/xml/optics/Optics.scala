package net.michalsitko.xml.optics

import monocle._
import net.michalsitko.xml.entities._
import scalaz.Applicative
import scalaz.syntax.traverse._

import scalaz.Applicative

object Optics {
  import scalaz.std.list._

  def deep(elementMatcher: NameMatcher): Traversal[LabeledElement, Element] = new Traversal[LabeledElement, Element] {
    override final def modifyF[F[_]: Applicative](f: (Element) => F[Element])(from: LabeledElement): F[LabeledElement] = {
      val modified = from.element.children.collect(modifyOnlyMatching(elementMatcher, f)).toList

      Applicative[F].sequence(modified).map { elements =>
        from.copy(element = from.element.copy(children = elements))
      }
    }
  }

  def deep(label: String): Traversal[LabeledElement, Element] =
    deep(NameMatcher.fromString(label))

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

  val isText: Prism[Node, Text] = Prism.partial[Node, Text]{
    case text: Text => text
  }(identity)

  val textIso: Iso[Text, String] = Iso[Text, String](_.text)(Text(_))

  val isTextS = isText.composeIso(textIso)

  val hasTextOnly: Optional[Element, String] = hasOneChild.composePrism(isTextS)

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

  // TODO: test if lawful
  val children = Lens[LabeledElement, Seq[Node]](_.element.children){ newChildren => from =>
    from.copy(element = from.element.copy(children = newChildren))
  }

  val nodeToNodeTraversal = new Traversal[Node, Node] {
    def modifyF[F[_]: Applicative](fun: Node => F[Node])(from: Node): F[Node] = {
      from match {
        case LabeledElement(label, element) =>
          val appOfList = element.children.toList.traverse(fun)
          appOfList.map { ch =>
            LabeledElement(label, element.copy(children = ch))
          }

        case t @ Text(_) =>
          Applicative[F].pure(t)

        case comment: Comment =>
          Applicative[F].pure(comment)
      }
    }
  }


  private def modifyOnlyMatching[F[_] : Applicative](elementMatcher: NameMatcher, f: Element => F[Element]): PartialFunction[Node, F[Node]] = {
    case el: LabeledElement if elementMatcher.matches(el.label) =>
      val modifiedElems = f(el.element)
      modifiedElems.map(modifiedElem => LabeledElement(el.label, modifiedElem))
    case anythingElse =>
      Applicative[F].pure(anythingElse)
  }


  private def onlyChild(element: Element): Option[Node] = {
    if (element.children.size == 1) {
      Some(element.children.head)
    } else {
      None
    }
  }

}
