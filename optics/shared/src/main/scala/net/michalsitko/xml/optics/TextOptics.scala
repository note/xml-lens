package net.michalsitko.xml.optics

import monocle.Iso
import net.michalsitko.xml.entities.Text

trait TextOptics {
  val textIso: Iso[Text, String] = Iso[Text, String](_.text)(Text(_))
}

object TextOptics extends TextOptics
