package pl.msitko.xml.parsing

abstract class XmlException(message: String, cause: Throwable) extends Exception(message, cause)

// Represents errors caused by parsing errors
final case class ParsingException(message: String, cause: Throwable) extends XmlException(message, cause)

// Represents errors caused by not being able to open a file represented by Path
final case class PathException(message: String, cause: Throwable) extends XmlException(message, cause)
