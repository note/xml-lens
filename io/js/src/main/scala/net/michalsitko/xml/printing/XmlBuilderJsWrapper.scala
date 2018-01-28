package net.michalsitko.xml.printing

import scala.scalajs.js
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("xmlbuilder/lib/index.js", JSImport.Namespace)
object XmlBuilderJsWrapper extends js.Object {
  def begin(config: js.Dictionary[Any], appendFn: js.Function1[String, Unit]): JsXmlDocument = js.native
  def stringWriter(): js.Dictionary[Any] = js.native
}

@js.native
trait JsXmlDocument extends js.Object {
  def dec(dict: js.Dictionary[String])                : JsXmlDocument = js.native
  def ele(name: String, attrs: js.Dictionary[String]) : JsXmlDocument = js.native
  def up()                                            : JsXmlDocument = js.native
  def txt(txt: String)                                : JsXmlDocument = js.native
  def ins(target: String, data: String)               : JsXmlDocument = js.native
  def dat(cdataString: String)                        : JsXmlDocument = js.native
  def entity(name: String, value: String)             : JsXmlDocument = js.native
  def com(comment: String)                            : JsXmlDocument = js.native
  def end()                                           : Unit          = js.native
}

object XmlBuilder {
  def withWrapper(config: XmlBuilderConfig)(fn: JsXmlDocument => Unit): String = {
    var buff = ""
    val wrapper = XmlBuilderJsWrapper.begin(config.toJsDict, s => buff += s)
    fn(wrapper)
    wrapper.end()
    buff
  }
}

// options as documented in last paragraph of this: https://github.com/oozcitak/xmlbuilder-js/wiki#converting-to-string
case class XmlBuilderConfig(
  pretty: Boolean,
  indent: String,
  offset: Int,
  newline: String,
  spacebeforeslash: String
) {
  def toJsDict: js.Dictionary[Any] = js.Dictionary(
    "pretty"            -> pretty,
    "indent"            -> indent,
    "offset"            -> offset,
    "newline"           -> newline,
    "spacebeforeslash"  -> spacebeforeslash
  )

}
