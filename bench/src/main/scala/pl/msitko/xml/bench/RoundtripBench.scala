package pl.msitko.xml.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class RoundtripBench {
  import Roundtrip._

  @Benchmark def roundTripWithLens = RoundtripLens.roundtrip(example.input)

  @Benchmark def roundtripWithStd = RoundtripStd.roundtrip(example.input)
}
