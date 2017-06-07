package net.michalsitko.utils

import cats.data.NonEmptyList
import org.scalacheck.Cogen

import scala.xml.{Elem, NodeSeq}

trait CogenInstances {
  implicit val nodeSeqCogen = Cogen[NodeSeq]((_ : NodeSeq).hashCode().toLong)
  implicit val nonEmptyListOfElemCogen = Cogen[NonEmptyList[Elem]]((_: NonEmptyList[Elem]).hashCode().toLong)
}
