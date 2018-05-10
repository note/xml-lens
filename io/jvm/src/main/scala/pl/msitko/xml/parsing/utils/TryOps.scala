package pl.msitko.xml.parsing.utils

import scala.util.{Failure, Success, Try}

// Needed just because of compatibility with scala 2.11
private [parsing] object TryOps {
  implicit class WithEither[T](tryInstance: Try[T]) {
    def asEither: Either[Throwable, T] = {
      tryInstance match {
        case Success(v)   => Right(v)
        case Failure(ex)  => Left(ex)
      }
    }
  }
}
