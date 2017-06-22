package net.michalsitko.xml.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class RoundtripStdBench {
  import Roundtrip._

  @Benchmark def roundtrip = RoundtripStd.roundtrip(example.input)
}
