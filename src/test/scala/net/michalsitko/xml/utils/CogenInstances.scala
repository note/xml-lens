package net.michalsitko.xml.utils

import net.michalsitko.xml.entities.Element
import org.scalacheck.Cogen

trait CogenInstances {
  implicit val elementCogen = Cogen[Element]((_: Element).hashCode().toLong)
}
