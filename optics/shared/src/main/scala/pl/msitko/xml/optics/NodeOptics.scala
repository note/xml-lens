package pl.msitko.xml.optics

import monocle.{Prism, Traversal}
import pl.msitko.xml.entities.{LabeledElement, Node, Text}

import scalaz.Applicative
import scalaz.std.list._
import scalaz.syntax.traverse._

trait NodeOptics {
  val isText: Prism[Node, Text] = Prism.partial[Node, Text]{
    case text: Text => text
  }(identity)

  val isTextS: Prism[Node, String] = isText.composeIso(TextOptics.textIso)

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
