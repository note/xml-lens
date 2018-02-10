package net.michalsitko.xml.bench

import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._

import scala.xml.XML

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class ParseBench {
  @Benchmark def parseWithLens = XmlParser.parse(Roundtrip.example.input).right.get

  @Benchmark def parseWithStd = XML.loadString(Roundtrip.example.input)
}
