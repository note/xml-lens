package pl.msitko.xml.parsing

import fastparse.{WhitespaceApi, all}
import fastparse.core.Parsed.{Failure, Success}
import pl.msitko.xml.entities.XmlDeclaration

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

  val xmlDecl = {
    val wsMatters = WhitespaceApi.Wrapper {
      import fastparse.all._
      NoTrace(Pass)
    }
    import wsMatters._

    /* According to https://www.w3.org/TR/xml/#NT-S White space is defined as:
     *
     * 	S	   ::=   	(#x20 | #x9 | #xD | #xA)+
     */
    val ws = {
      val whiteSpaces = List(0x20, 0x9, 0xD, 0xA).map(_.toChar)

      P(CharIn(whiteSpaces).rep(min = 1))
    }

    P ( (ws.rep ~ versionInfo ~ ws.rep ~ End).map(v => (v, Option.empty[String])) | (ws.rep ~ versionInfo ~ ws.rep(min = 1) ~ encodingDecl ~ ws.rep ~ End).map(t => (t._1, Some(t._2))) )
  }


  def parse(input: String): Option[XmlDeclaration] =
    xmlDecl.parse(input) match {
      case Success((version, encodingOpt), _) => Some(XmlDeclaration(version, encodingOpt))
      case Failure(_, _, _) => None
    }

}
