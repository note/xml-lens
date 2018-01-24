package net.michalsitko.xml.parsing

import net.michalsitko.xml.entities.XmlDeclaration
import net.michalsitko.xml.test.utils.BaseSpec

class XmlDeclarationParserSpec extends BaseSpec {
  import XmlDeclarationParser._

  "parse" should {
    "work" in {
      parse("""version="1.0" encoding="UTF-8"""") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
    }
  }
}
