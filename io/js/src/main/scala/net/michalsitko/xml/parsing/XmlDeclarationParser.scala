package net.michalsitko.xml.parsing

import fastparse.{WhitespaceApi, all}
import fastparse.core.Parsed.{Failure, Success}
import net.michalsitko.xml.entities.XmlDeclaration

object XmlDeclarationParser {
  val White = WhitespaceApi.Wrapper{
    import fastparse.all._
    NoTrace(" ".rep)
  }
  import fastparse.noApi._
  import White._

  // see https://www.w3.org/TR/xml/#sec-prolog-dtd
  val versionNum: all.Parser[String] = P ( ("1." ~ CharIn('0' to '9').rep(min = 1)).!)
  val versionNumber = P ( ( CharIn(List('"')) ~ versionNum ~ CharIn(List('"') ) ) | (CharIn("'") ~ versionNum ~ CharIn("'")))
  val versionInfo = P ( "version" ~ "=" ~ versionNumber)

  val alpha = P( CharIn('a' to 'z') |  CharIn('A' to 'Z'))
  val encName: all.Parser[String] = P ( (alpha ~ (alpha | CharIn('0' to '9') | CharIn("-._")).rep(min = 0)).! )
  val encodingName = P (( CharIn(List('"')) ~ encName ~ CharIn(List('"'))) | (CharIn("'") ~ encName ~ CharIn("'")))
  val encodingDecl = P ( "encoding" ~ "=" ~ encodingName )

  val xmlDecl: all.Parser[(String, Option[String])] = P (versionInfo ~ encodingDecl.?)


  def parse(input: String): Option[XmlDeclaration] =
    xmlDecl.parse(input) match {
      case Success((version, encodingOpt), _) => Some(XmlDeclaration(version, encodingOpt))
      case Failure(_, _, _) => None
    }

}
