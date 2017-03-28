package net.michalsitko

import monocle.Prism

sealed trait Json
case object JNull extends Json
case class JStr(v: String) extends Json
case class JNum(v: Double) extends Json
case class JObj(v: Map[String, Json]) extends Json

object PrimsTest {
  def main(args: Array[String]): Unit = {
    val jStr = Prism[Json, String]{
      case JStr(v) => Some(v)
      case _       => None
    }(JStr)
  }
}
