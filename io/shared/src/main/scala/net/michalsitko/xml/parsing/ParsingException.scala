package net.michalsitko.xml.parsing

case class ParsingException(message: String, cause: Throwable) extends Exception(message, cause)
