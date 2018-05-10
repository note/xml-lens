package pl.msitko.xml.bench

import java.io.{File, StringWriter}
import java.nio.file.Paths
import java.util.concurrent.TimeUnit

import org.openjdk.jmh.annotations._
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter

import scala.xml.XML

@BenchmarkMode(Array(Mode.AverageTime))
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
class HugeRoundTripBench {
  import HugeRoundTripBench._

  @Benchmark def roundTripWithLens = {
    val parsed = XmlParser.parsePath(path).right.get
    XmlPrinter.print(parsed)
  }

  @Benchmark def roundtripWithStd = {
    val xml = XML.loadFile(file)

    val writer = new StringWriter
    XML.write(writer, xml, "UTF-8", true, null)
    writer.toString
  }
}

object HugeRoundTripBench {
  // BEWARE: that file is not included in the repo because of its huge size
  // you can download some big XMLs at https://dumps.wikimedia.org/enwiki/
  val path = Paths.get("src", "main", "resources", "enwiki-20180420-pages-articles26.xml")

  val file = {
    // BEWARE: that file is not included in the repo because of its huge size
    // you can download some big XMLs at https://dumps.wikimedia.org/enwiki/
    val p = List("src", "main", "resources", "enwiki-20180420-pages-articles26.xml").mkString(File.separator)
    new File(p)
  }
}
