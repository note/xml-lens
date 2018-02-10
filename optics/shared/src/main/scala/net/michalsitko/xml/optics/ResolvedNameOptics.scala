package net.michalsitko.xml.optics

import monocle.Lens
import net.michalsitko.xml.entities.ResolvedName

trait ResolvedNameOptics {
  val localName = Lens[ResolvedName, String](_.localName)(newLocalName => from => from.copy(localName = newLocalName))
}

object ResolvedNameOptics extends ResolvedNameOptics
