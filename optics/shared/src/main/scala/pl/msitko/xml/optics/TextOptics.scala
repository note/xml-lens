package pl.msitko.xml.optics

import monocle.Iso
import pl.msitko.xml.entities.Text

trait TextOptics {
  val textIso: Iso[Text, String] = Iso[Text, String](_.text)(Text(_))
}

object TextOptics extends TextOptics
