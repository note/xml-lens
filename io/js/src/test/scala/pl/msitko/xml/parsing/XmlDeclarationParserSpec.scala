package pl.msitko.xml.parsing

import pl.msitko.xml.entities.XmlDeclaration
import pl.msitko.xml.test.utils.BaseSpec

class XmlDeclarationParserSpec extends BaseSpec {
  import XmlDeclarationParser._

  "parse" should {
    "work" in {
      parse("""version="1.0" encoding="UTF-8"""") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("""version='1.0' encoding='UTF-8'""") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("""version="1.0" encoding="ISO-8859-1"""") should === (Some(XmlDeclaration("1.0", Some("ISO-8859-1"))))
      parse("""version="1.1" encoding="ISO-8859-1"""") should === (Some(XmlDeclaration("1.1", Some("ISO-8859-1"))))
      parse("""version="1.1" encoding="ISO-8859-1"""") should === (Some(XmlDeclaration("1.1", Some("ISO-8859-1"))))
    }

    "work without encoding" in {
      parse("""version="1.0"""") should === (Some(XmlDeclaration("1.0", None)))
      parse("""version="1.0"   """) should === (Some(XmlDeclaration("1.0", None)))
      parse("""    version = "1.0"   """) should === (Some(XmlDeclaration("1.0", None)))
      parse("""    version = '1.1'   """) should === (Some(XmlDeclaration("1.1", None)))
    }

    "ignore whitespaces" in {
      parse("""version="1.0"   encoding = "UTF-8"""") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("""     version="1.0" encoding="UTF-8"""") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("""     version = "1.0"    encoding  = "UTF-8"   """) should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("""version  =   "1.0" encoding="UTF-8"""") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("version  =   \"1.0\" \n encoding=\"UTF-8\"") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
      parse("version  =   \"1.0\"\tencoding=\"UTF-8\"") should === (Some(XmlDeclaration("1.0", Some("UTF-8"))))
    }

    "fail" in {
      parse("") should === (None)
      parse("""encoding="ISO-8859-1"""") should === (None)
      parse("""version encoding="ISO-8859-1"""") should === (None)
      parse("""version= encoding="ISO-8859-1"""") should === (None)
      parse("""version="" encoding="ISO-8859-1"""") should === (None)
      parse("""version="abc" encoding="ISO-8859-1"""") should === (None)
      parse("""version=abc""") should === (None)
      parse("""version=1.0 encoding="UTF-8"""") should === (None)
      parse("""version="1.0" encoding=UTF-8""") should === (None)
    }

    "fail if no whitespace between version and encoding" in {
      parse("""version="1.0"encoding="UTF-8"""") should === (None)
    }

    "fail for empty encoding" in {
      parse("""version="1.0" encoding=""""") should === (None)
    }
  }
}
