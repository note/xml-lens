package net.michalsitko.xml.bench

import org.scalatest.{FlatSpec, Matchers}


class SimpleTransformationSpec extends FlatSpec with Matchers {
  import SimpleTransformation._

  "SimpleTransformation" should "work" in {
    val withLens = transformWith(SimpleTransformationLens)
    val withStd  = transformWith(SimpleTransformationStd).replace(''', '"')

    withLens should equal (withStd)
  }

  def transformWith(transformer: => SimpleTransformation): String = {
    transformer.transform(example.input)
  }
}
