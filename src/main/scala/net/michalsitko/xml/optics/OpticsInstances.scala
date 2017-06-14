package net.michalsitko.xml.optics

import monocle.Traversal
import monocle.function.Plated
import net.michalsitko.xml.entities._

trait OpticsInstances {
  implicit val nodePlated: Plated[Node] = new Plated[Node] {
    override def plate: Traversal[Node, Node] = new Traversal[Node, Node] {
      import scalaz.Applicative
      import scalaz.std.list._
      import scalaz.syntax.traverse._

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
  }
}

object OpticsInstances extends OpticsInstances
