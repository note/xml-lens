package pl.msitko.xml.printing

// https://stackoverflow.com/questions/1091945/what-characters-do-i-need-to-escape-in-xml-documents
private [printing] object Escaper {
  // instead of simplistic `.replace("&", "&amp;")` which is incorrect as will do substitution even
  // for entity references we should consider using sth like following regex:
  // val raw = raw"\&(?![a-zA-Z:_][a-zA-Z0-9:_\-\.]*;)".r
  // it's not used at the moment as the effect on performance is unknown (esp. relevant for escaping text
  // nodes which may be huge
  // I noticed javax.xml.stream.XMLStreamWriter does the same naive thing Escaper does here so
  // probably it's not that bad

  def escapeAttributeValue(value: String): String = {
    value
      .replace("&", "&amp;") // It's crucial for correctness that replacing `&` is the first replacement we perform
      .replace("<", "&lt;")
      .replace(">", "&gt;")
      .replace("\"", "&quot;")
  }

  def escapeText(text: String): String = {
    text
      .replace("&", "&amp;") // It's crucial for correctness that replacing `&` is the first replacement we perform
      .replace("<", "&lt;")
      .replace(">", "&gt;")
  }
}
