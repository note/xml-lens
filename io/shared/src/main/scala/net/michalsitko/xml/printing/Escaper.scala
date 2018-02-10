package net.michalsitko.xml.printing

// https://stackoverflow.com/questions/1091945/what-characters-do-i-need-to-escape-in-xml-documents
object Escaper {
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
