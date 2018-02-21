---
layout: docs
title:  "Parsing and Printing"
position: 3
---

## Parsing

You need to have `xml-lens-io` included.

For JVM platform `xml-lens-io` uses `javax.xml.stream.XMLStreamReader`. To include it in your build add the following to 
your `build.sbt`:

```
libraryDependencies += "net.michalsitko" %% "xml-lens-io" % xmlLensVersion
```

For JS platform slightly modified version of [sax-js](https://github.com/isaacs/sax-js) is used underneath. To include it
in your build add the following to your `build.sbt`:

```
libraryDependencies += "net.michalsitko" %%% "xml-lens-io" % xmlLensVersion
```

After you included `io` module to your project parsing XML boils down to:

```tut:book
import net.michalsitko.xml.parsing.XmlParser

val input = "<a><b>this is xml</b></a>"

XmlParser.parse(input)
```

### Differences between JVM and JS

#### Parsing entity references

On JVM following input:

```tut:silent
val input =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "This <em>is</em> an entity.">
      |]><html><body><p>abc &test-entity; def</p></body></html>""".stripMargin
```

After parsing, element `p` will have 3 children: 

```(Text("abc "), EntityReference("test-entity", "This <em>is</em> an entity."), Text(" def"))```

As you can see parser created `EntityReference` which holds both name of the entity and the replacement. This is the default
behavior as it does not lose any information which is especially useful if you want to make non-intrusive changes
and print back document as similar to original as it's possible. Read more about configuring this behavior at 
[parsing configuration](#parsing-configuration).

### Parsing configuration 

*At the moment only JVM parser is configurable.* Configuration is done by passing implicit parameter of type
`ParserConfig` to `XmlParser.parse` method. If no configuration is accessible in scope `ParserConfig.Default` is used.

#### `replaceEntityReferences`

As of now the only `ParserConfig` has only one property - `replaceEntityReferences`. It controls how entity references
are parsed. The default value is `false`. What result is expected in that case was described in [Parsing entity references](#parsing-entity-references).
Here we focus on `replaceEntityReferences = true` case.

```tut:silent
val input =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<!DOCTYPE html
      |    PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN"
      |    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
      |[
      |    <!ENTITY test-entity "This <em>is</em> an entity.">
      |]><html><body><p>abc &test-entity; def</p></body></html>""".stripMargin
      
import net.michalsitko.xml.parsing.ParserConfig

implicit val cfg = ParserConfig.Default.copy(replaceEntityReferences = true)

XmlParser.parse(input)
```

When parsed, element `p` will have just one child:

```Text("abc This <em>is</em> an entity. def")```

## Printing

You need to have `xml-lens-io` included.

To add it to your build add following to your `build.sbt`:

```
libraryDependencies += "net.michalsitko" %% "xml-lens-io" % xmlLensVersion
```

Or (in case of scala.js build):

```
libraryDependencies += "net.michalsitko" %%% "xml-lens-io" % xmlLensVersion
``` 

The same printing code written in Scala is used for both JVM and JS. Thus, no differences between platforms is expected
in terms of printing.  

To print XML:

```tut:book
import net.michalsitko.xml.entities.{LabeledElement, XmlDocument}
import net.michalsitko.xml.printing.XmlPrinter

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
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter

// xml formatting is strange here, let's assume we want to keep it
val input = """|<a someAttr="someVal">
               |       <b>someText  </b>
               |<c>otherText</c></a>""".stripMargin
               
XmlParser.parse(input).map { doc =>
  XmlPrinter.print(doc)
}
```

#### How to pretty print

```tut:book
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.{Indent, PrinterConfig, XmlPrinter}

// xml formatting is strange here, let's assume we want to keep it
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
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.syntax.document._

// xml formatting is strange here, let's assume we don't care about it and want to have some minimized output
val input = """|<a someAttr="someVal">
               |       <b>someText  </b>
               |<c>otherText</c></a>""".stripMargin
               
XmlParser.parse(input).map { doc =>
  val minimized = doc.minimize
  XmlPrinter.print(minimized)
}
```
