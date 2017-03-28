package net.michalsitko

import monocle.Lens

object MonocleTest {
  val hardcoded = 999
  val sn = Lens[Address, Int](_.streetNumber)(number => a => a.copy(streetNumber = number))

  def main(args: Array[String]): Unit = {
    val address = Address(44, "Zielona")

    val newAddress = sn.set(55)(address)
    println("new address: " + newAddress)
    println("new number: " + sn.get(newAddress))

    val modifiedAddress = sn.modify(_ + 100)(address)
    println("modified address: " + modifiedAddress)
  }

  case class Address(streetNumber: Int, streetName: String)
}
