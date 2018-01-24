package net.michalsitko.xml.parsing

import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.scalajs.js.annotation.JSImport

@js.native
@JSImport("sax/lib/sax.js", JSImport.Namespace)
private [parsing] object sax extends js.Object {
  def parser(strict: Boolean, dictionary: js.Dictionary[Boolean]): parser = js.native
}

@js.native
private [parsing] trait Writer extends js.Object {
  def close(): Unit = js.native
}

@js.native
//@JSImport("sax.js", "parser") // TODO: probably to remove
private [parsing] trait parser extends js.Object {
  var ontext: js.Function1[String, Unit] = js.native
  var onprocessinginstruction: js.Function1[JsProcessingInstruction, Unit] = js.native
  var onsgmldeclaration: js.Function0[Unit] = js.native
  var ondoctype: js.Function0[Unit] = js.native
  var oncomment: js.Function0[Unit] = js.native
  var onopentagstart: js.Function0[Unit] = js.native
  var onattribute: js.Function1[JsAttribute, Unit] = js.native
  var onopentag: js.Function1[JsNode, Unit] = js.native
  var onclosetag: js.Function0[Unit] = js.native
  var onopencdata: js.Function0[Unit] = js.native
  var oncdata: js.Function0[Unit] = js.native
  var onclosecdata: js.Function0[Unit] = js.native
  var onerror: js.Function1[js.Error, Unit] = js.native
  var onend: js.Function0[Unit] = js.native
  var onready: js.Function0[Unit] = js.native
  var onscript: js.Function0[Unit] = js.native
  var onopennamespace: js.Function0[Unit] = js.native
  var onclosenamespac: js.Function0[Unit] = js.native

  def write(input: String): Writer = js.native
}

@js.native
private [parsing] trait JsProcessingInstruction extends js.Object {
  val name: String = js.native
  val body: String = js.native
}

@js.native
private [parsing] trait JsNode extends js.Object {
  val name: String = js.native
  val attributes: js.Dictionary[JsAttribute] = js.native
  val ns: js.Dictionary[String] = js.native

  // If the xmlns option is set, then it will contain namespace binding information on the ns member, and will have a local, prefix, and uri member.
  val local: String = js.native
  val prefix: String = js.native
  val uri: String = js.native
}

@js.native
private [parsing] trait JsAttribute extends js.Object {
  val name: String = js.native // e.g. `prefix:attrName`
  val value: String = js.native
  val prefix: String = js.native
  val local: String = js.native
  val uri: String = js.native
}

object JsParser {
  def apply(strict: Boolean, options: JsParserOptions): parser = {
    sax.parser(strict, options.toDict)
  }
}

case class JsParserOptions(
  trim: Option[Boolean] = None,
  normalize: Option[Boolean] = None,
  lowercase: Option[Boolean] = None,
  xmlns: Option[Boolean] = None,
  position: Option[Boolean] = None,
  strictEntities: Option[Boolean] = None
) {
  def toDict: js.Dictionary[Boolean] = {
    val empty = Map.empty[String, Boolean]

    val fields = List(
      trim.fold(empty)(b => Map("trim" -> b)),
      normalize.fold(empty)(b => Map("normalize" -> b)),
      lowercase.fold(empty)(b => Map("lowercase" -> b)),
      xmlns.fold(empty)(b => Map("xmlns" -> b)),
      position.fold(empty)(b => Map("position" -> b)),
      strictEntities.fold(empty)(b => Map("strictEntities" -> b))
    )

    fields.reduce(_ ++ _).toJSDictionary
  }
}
