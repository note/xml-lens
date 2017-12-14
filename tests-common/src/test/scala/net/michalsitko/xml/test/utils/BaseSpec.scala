package net.michalsitko.xml.test.utils

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{Matchers, WordSpec}

trait BaseSpec extends WordSpec with Matchers with TypeCheckedTripleEquals
