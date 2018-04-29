package pl.msitko.xml.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class SimpleTransformationBench {
  import SimpleTransformation._

  @Benchmark def transformWithLens = SimpleTransformationLens.transform(example.input)

  @Benchmark def transformWithStd = SimpleTransformationStd.transform(example.input)
}
