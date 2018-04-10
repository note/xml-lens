package pl.msitko.xml.parsing

final case class ParsingException(message: String, cause: Throwable) extends Exception(message, cause)
