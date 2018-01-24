package net.michalsitko.xml.parsing

import net.michalsitko.xml.entities.XmlDeclaration
import net.michalsitko.xml.test.utils.BaseSpec

class XmlDeclarationParserSpec extends BaseSpec {
  import XmlDeclarationParser._

  "parse" should {
    "work" in {
      parse("""version="1.0" encoding="UTF-8"""") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("""version="1.0" encoding="ISO-8859-1"""") should === (Some(XmlDeclaration("1.0", Some("ISO-8859-1"))))
      parse("""version="1.1" encoding="ISO-8859-1"""") should === (Some(XmlDeclaration("1.1", Some("ISO-8859-1"))))
      parse("""version="1.1" encoding="ISO-8859-1"""") should === (Some(XmlDeclaration("1.1", Some("ISO-8859-1"))))
    }

    "work without encoding" in {
      parse("""version="1.0""""") should === (Some(XmlDeclaration("1.0", None)))
    }

    "ignore whitespaces" in {
      parse("""version="1.0"   encoding = "UTF-8"""") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("""     version="1.0" encoding="UTF-8"""") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("""     version = "1.0"    encoding  = "UTF-8"   """) should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("""version  =   "1.0" encoding="UTF-8"""") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
    }

    "fail" in {
      parse("") should === (None)
      parse("""encoding="ISO-8859-1"""") should === (None)
      parse("""version encoding="ISO-8859-1"""") should === (None)
      parse("""version= encoding="ISO-8859-1"""") should === (None)
      parse("""version="" encoding="ISO-8859-1"""") should === (None)
      parse("""version="abc" encoding="ISO-8859-1"""") should === (None)

      parse("""version="1.0"encoding="UTF-8"""") should === (None)
    }

    "work 2" in {
      parse("""version="1.0" encoding=""""") should === (None)
    }
  }
}
