package pl.msitko.xml.bench

trait SmallRoundtrip {
  def roundtrip(input: String): String
}

object SmallRoundtrip {
  def example = {
    Example(SomeXml.someXml, SomeXml.someXml)
  }
}
