package net.michalsitko.xml.utils

import net.michalsitko.xml.entities.{LabeledElement, ResolvedName}
import org.scalacheck.Gen

trait ArbitraryInstances {
  def arbitraryElement(depth: Int, label: ResolvedName): Gen[LabeledElement] = {
    ???
  }

}
