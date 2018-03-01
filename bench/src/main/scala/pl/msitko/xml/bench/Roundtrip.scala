package pl.msitko.xml.bench

trait Roundtrip {
  def roundtrip(input: String): String
}

object Roundtrip {
  def example = {
    Example(SomeXml.someXml, SomeXml.someXml)
  }
}
