package net.michalsitko.xml.bench

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FlatSpec, Matchers}


class SimpleTransformationSpec extends FlatSpec with Matchers with TypeCheckedTripleEquals {
  import SimpleTransformation._

  "SimpleTransformation" should "work" in {
    val withLens = transformWith(SimpleTransformationLens)
    val withStd  = transformWith(SimpleTransformationStd).replace(''', '"')

    withLens should === (example.output)
    withLens should === (withStd)
  }

  def transformWith(transformer: => SimpleTransformation): String = {
    transformer.transform(example.input)
  }
}
