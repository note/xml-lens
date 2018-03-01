package pl.msitko.xml.bench

trait SimpleTransformation {
  def transform(input: String): String
}

object SimpleTransformation {
  def example = {
    val input =
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a>
        |  <e>item</e>
        |  <f>item</f>
        |  <g>item</g>
        |</a>""".stripMargin

    val output =
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a>
        |  <e>item</e>
        |  <f>ITEM</f>
        |  <g>item</g>
        |</a>""".stripMargin

    Example(input, output)
  }

}
