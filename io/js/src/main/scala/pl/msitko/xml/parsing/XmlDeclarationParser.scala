package pl.msitko.xml.parsing

import fastparse._
import fastparse.Parsed.{Failure, Success}
import pl.msitko.xml.entities.XmlDeclaration

object XmlDeclarationParser {
  //implicit val whitespace = { implicit ctx: ParsingRun[_] =>
    //CharsWhileIn(" ", 0)
  //}
  import SingleLineWhitespace._

  // see https://www.w3.org/TR/xml/#sec-prolog-dtd
  def versionNum[_: P] = P ( ("1." ~ CharIn("0-9").rep(1)).! )
  def versionNumber[_: P] = P ( ( "\"" ~ versionNum ~ "\"" ) | ("'" ~ versionNum ~ "'") )
  def versionInfo[_: P] = P ( "version" ~ "=" ~ versionNumber)

  def alpha[_: P] = P( CharIn("a-zA-Z"))
  def encName[_: P] = P ( (alpha ~ (alpha | CharIn("0-9") | CharIn("\\-._")).rep(0)).! )
  def encodingName[_: P] = P (( "\"" ~ encName ~ "\"") | ("'" ~ encName ~ "'") )
  def encodingDecl[_: P] = P ( "encoding" ~ "=" ~ encodingName )

  def xmlDecl[_: P] = {
    import NoWhitespace._

    /* According to https://www.w3.org/TR/xml/#NT-S White space is defined as:
     *
     * 	S	   ::=   	(#x20 | #x9 | #xD | #xA)+
     *               space-tabulation-newline-linefeed
     */
    def ws = P(CharIn("\u0020\u0009\u000D\u000A").rep(1) )

    P ( (ws.rep ~ versionInfo ~ ws.rep ~ End).map(v => (v, Option.empty[String])) | (ws.rep ~ versionInfo ~ ws.rep(1) ~ encodingDecl ~ ws.rep ~ End).map(t => (t._1, Some(t._2))) )
  }


  def parse(input: String): Option[XmlDeclaration] =
    fastparse.parse(input, xmlDecl(_)) match {
      case Success((version, encodingOpt), _) => Some(XmlDeclaration(version, encodingOpt))
      case Failure(_, _, _) => None
    }

}
