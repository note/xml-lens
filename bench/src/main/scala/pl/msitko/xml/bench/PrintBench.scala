package pl.msitko.xml.bench

//import java.io.StringWriter
import java.io.StringWriter
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import pl.msitko.xml.entities.XmlDocument
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter

import scala.xml.{Elem, XML}

object PrintBenchParams {
  val lensElement: XmlDocument =
    XmlParser.parse(Roundtrip.example.input).right.get

  val stdElement: Elem =
    XML.loadString(Roundtrip.example.input)
}

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class PrintBench {
  import PrintBenchParams._

  @Benchmark def printWithLens: String = {
    XmlPrinter.print(lensElement)
  }

//  @Benchmark def prettyPrintWithLens: String = {
//    XmlPrinter.print(lensElement)
//  }

  @Benchmark def prettyPrintWithStd: String = {
    val writer = new StringWriter
    XML.write(writer, stdElement, "UTF-8", true, null)
    writer.toString
  }

}
