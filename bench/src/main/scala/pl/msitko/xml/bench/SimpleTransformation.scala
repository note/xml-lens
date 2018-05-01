package pl.msitko.xml.bench

trait SimpleTransformation {
  def transform(input: String): String
}

object SimpleTransformation {
  def example = {
    val input =
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a>
        |  <boring>
        |    <special>text</special>
        |  </boring>
        |  <interesting>
        |    <special>text</special>
        |    <boring>text</boring>
        |  </interesting>
        |  <special>text</special>
        |</a>""".stripMargin

    val output =
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a>
        |  <boring>
        |    <special>text</special>
        |  </boring>
        |  <interesting>
        |    <special>TEXT</special>
        |    <boring>text</boring>
        |  </interesting>
        |  <special>text</special>
        |</a>""".stripMargin

    Example(input, output)
  }

}
