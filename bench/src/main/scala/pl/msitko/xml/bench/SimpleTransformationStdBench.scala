package pl.msitko.xml.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class SimpleTransformationStdBench {
  import SimpleTransformation._

  @Benchmark def transform = SimpleTransformationStd.transform(example.input)
}
