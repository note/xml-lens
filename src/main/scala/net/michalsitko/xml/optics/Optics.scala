package net.michalsitko.xml.optics

import monocle._
import net.michalsitko.xml.entities._

import scalaz.Applicative

object Optics {
  import scalaz.std.list._
  import scalaz.syntax.traverse._
  import scalaz.syntax.applicative._


  def deep(label: String): Traversal[LabeledElement, Element] = deep(ResolvedName.unprefixed(label))

  def deep(label: ResolvedName): Traversal[LabeledElement, Element] = new Traversal[LabeledElement, Element] {
    override final def modifyF[F[_]](f: (Element) => F[Element])(from: LabeledElement)(implicit F: Applicative[F]): F[LabeledElement] = {
      val tmp = from.element.children.collect {
        // TODO: equals for ResolvedName should return true for different prefixes but same fields otherwise
        case el: LabeledElement if el.label == label =>
          val mapped = f(el.element)
          F.map(mapped)(mappedDetail => LabeledElement(label, mappedDetail).asInstanceOf[Node])
        case anythingElse: Node =>
          F.pure(anythingElse)
      }.toList

      F.map(F.sequence(tmp)){ elements =>
        from.copy(element = from.element.copy(children = elements))
      }
    }
  }

  def deeper(label: String): Traversal[Element, Element] = deeper(ResolvedName.unprefixed(label))

  def deeper(label: ResolvedName): Traversal[Element, Element] = new Traversal[Element, Element] {
    override final def modifyF[F[_]](f: (Element) => F[Element])(from: Element)(implicit F: Applicative[F]): F[Element] = {
      val tmp = from.children.collect {
        // TODO: equals for ResolvedName should return true for different prefixes but same fields otherwise
        case el: LabeledElement if el.label == label =>
          val mapped = f(el.element)
          F.map(mapped)(mappedDetail => LabeledElement(label, mappedDetail).asInstanceOf[Node])
        case anythingElse =>
          F.pure(anythingElse)
      }.toList

      F.map(F.sequence(tmp)){ elements =>
        from.copy(children = elements)
      }
    }
  }

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

  private def onlyChild(element: Element): Option[Node] = {
    if (element.children.size == 1) {
      Some(element.children.head)
    } else {
      None
    }
  }

  val hasTextOnly: Optional[Element, String] = hasOneChild.composePrism(isTextS)

  def attribute(key: String): Optional[Element, String] = attribute(ResolvedName.unprefixed(key))

  def attribute(key: ResolvedName) = Optional[Element, String] { el =>
    el.attributes.find(_.key == key).map(_.value)
  }{ newValue => from =>
    val newAttributes = from.attributes.collect {
      case attr@Attribute(`key`, _) =>
        attr.copy(value = newValue)
      case attr =>
        attr
    }

    from.copy(attributes = newAttributes)
  }

  val attributes: Lens[Element, Seq[Attribute]] =
    Lens[Element, Seq[Attribute]](_.attributes)(newAttrs => from => from.copy(attributes = newAttrs))

}
