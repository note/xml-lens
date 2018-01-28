package net.michalsitko.xml.printing

import net.michalsitko.xml.entities.XmlDeclaration

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("xmlbuilder/lib/index.js", JSImport.Namespace)
object XmlBuilderJsWrapper extends js.Object {
  def begin(appendFn: js.Function1[String, Unit]): JsXmlDocument = js.native
}

@js.native
trait JsXmlDocument extends js.Object {
  def dec(dict: js.Dictionary[String]): JsXmlDocument = js.native
  def ele(name: String, attrs: js.Dictionary[String]): JsXmlDocument = js.native
  def up(): JsXmlDocument = js.native
  def end(): Unit = js.native
}

@js.native
private [printing] trait JsDeclaration extends js.Object {
  var version: String
  var encoding: Option[String]
}

object ToXmlBuilderJsConverter {
  def fromXmlDeclaration(xmlDeclaration: XmlDeclaration): js.Dictionary[String] =
    (Map("version" -> xmlDeclaration.version) ++
      xmlDeclaration.encoding.fold(Map.empty[String, String])(v => Map("encoding" -> v))
    ).toJSDictionary
}