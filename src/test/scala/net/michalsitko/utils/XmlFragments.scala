package net.michalsitko.utils

trait XmlFragments {
  val xmlAsString =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |  <b>
      |    <c>
      |      <d>
      |        <e1>
      |          <f>item1</f>
      |          <g>item2</g>
      |        </e1>
      |        <e2>
      |          <f>item1</f>
      |          <g>item2</g>
      |          <h>item3</h>
      |          <f>item1</f>
      |        </e2>
      |        <e2>
      |          <f>item1</f>
      |          <g>item2</g>
      |          <h>item3</h>
      |          <f>item1</f>
      |        </e2>
      |      </d>
      |      <s>summary</s>
      |    </c>
      |  </b>
      |</a>
    """.stripMargin

  val simpleAsString =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c1>
      |      <f>item1</f>
      |      <h>item2</h>
      |   </c1>
      |   <c2>
      |      <f>item1</f>
      |      <g>item2</g>
      |      <h>item3</h>
      |   </c2>
      |   <s>summary</s>
      |</a>
    """.stripMargin

  val verySimpleString =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item1</f>
      |      <g>item2</g>
      |   </c1>
      |   <c2>
      |      <f>item1</f>
      |      <g>item2</g>
      |      <h>item3</h>
      |   </c2>
      |   <s>summary</s>
      |</a>
    """.stripMargin

  // TODO: to remove
  val xmlString1 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <f>item1</f>
      |   </c1>
      |</a>
    """.stripMargin

  val xmlString2 =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |   <c1>
      |      <y>item1</y>
      |   </c1>
      |</a>
    """.stripMargin

  val asLiteral =
    <a>
      <b>
        <c>
          <d>
            <e1>
              <f>item1</f>
              <g>item2</g>
            </e1>
            <e2>
              <f>item1</f>
              <g>item2</g>
              <h>item3</h>
            </e2>
          </d>
          <s>summary</s>
        </c>
      </b>
    </a>
}

object XmlFragments extends XmlFragments
