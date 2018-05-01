package pl.msitko.xml.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class SmallRoundtripBench {
  import SmallRoundtrip._

  @Benchmark def roundTripWithLens = SmallRoundtripLens.roundtrip(example.input)

  @Benchmark def roundtripWithStd = SmallRoundtripStd.roundtrip(example.input)
}
