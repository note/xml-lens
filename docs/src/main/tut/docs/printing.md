---
layout: docs
title:  "Printing"
position: 6
---

## Printing

You need to have `xml-lens-io` included.

To add it to your build add following to your `build.sbt`:

```
libraryDependencies += "pl.msitko" %% "xml-lens-io" % xmlLensVersion
```

Or (in case of scala.js build):

```
libraryDependencies += "pl.msitko" %%% "xml-lens-io" % xmlLensVersion
``` 

The same printing code written in Scala is used for both JVM and JS. Thus, no differences between platforms is expected
in terms of printing.  

To print XML:

```tut:book
import pl.msitko.xml.entities.{LabeledElement, XmlDocument}
import pl.msitko.xml.printing.XmlPrinter

val document = XmlDocument.noProlog(LabeledElement.unprefixed("root"))

XmlPrinter.print(document)
```

### Printing configuration

Some behaviors of `XmlPrinter.print` can be configured with `PrinterConfig`. It can be passed as implicit parameter.
When no implicit config can be found `PrinterConfig.Default` is used. To find out more about `PrinterConfig` please
read API docs of it.

### Printing cookbook

#### How to print back output which is possibly the most similar to input

There may be situations in which you want to introduce as few formatting changes as possible. In fact default
configurations for both parser and printer are well suited for that purpose so the following code will do that:

```tut:book
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter

// xml formatting is strange here, let us assume we want to keep it
val input = """|<a someAttr="someVal">
               |       <b>someText  </b>
               |<c>otherText</c></a>""".stripMargin
               
XmlParser.parse(input).map { doc =>
  XmlPrinter.print(doc)
}
```

#### How to pretty print

```tut:book
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.{Indent, PrinterConfig, XmlPrinter}

// xml formatting is strange here, let us assume we want to convert to pretty formatting
val input = """|<a someAttr="someVal">
               |       <b>someText  </b>
               |<c>otherText</c></a>""".stripMargin
               
XmlParser.parse(input).map { doc =>
  implicit val printCfg = PrinterConfig.Default.copy(indent = Indent.IndentWith("  "))
  XmlPrinter.print(doc)
}
```

#### How to print minimized XML

`xml-lens` takes a little bit unusual approach to minimization and considers it not a property of printing but a 
transformation performed on document structure (i.e. AST). It does so partially because XML is in theory whitespace
sensitive and thus treating whitespaces as printing details does not seem appropriate.

In short to parse XML, minimize it and print it back you need to:

```tut:book
import pl.msitko.xml.printing.XmlPrinter
import pl.msitko.xml.printing.XmlPrinter
import pl.msitko.xml.dsl._

// xml formatting is strange here, let us assume we don't care about it and want to have some minimized output
val input = """|<a someAttr="someVal">
               |       <b>someText  </b>
               |<c>otherText</c></a>""".stripMargin
               
XmlParser.parse(input).map { doc =>
  val minimized = doc.minimize
  XmlPrinter.print(minimized)
}
```
