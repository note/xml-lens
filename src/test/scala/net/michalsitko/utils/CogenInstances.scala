package net.michalsitko.utils

import org.scalacheck.Cogen

import scala.xml.NodeSeq

trait CogenInstances {
  implicit val nodeSeqCogen = Cogen[NodeSeq]((_ : NodeSeq).hashCode().toLong)
}
