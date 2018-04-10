---
layout: docs
title:  "Parsing"
position: 4
---

## Parsing

You need to have `xml-lens-io` included.

For JVM platform `xml-lens-io` uses `javax.xml.stream.XMLStreamReader`. To include it in your build add the following to 
your `build.sbt`:

```
libraryDependencies += "pl.msitko" %% "xml-lens-io" % xmlLensVersion
```

For JS platform slightly modified version of [sax-js](https://github.com/isaacs/sax-js) is used underneath. To include it
in your build add the following to your `build.sbt`:

```
libraryDependencies += "pl.msitko" %%% "xml-lens-io" % xmlLensVersion
```

After you included `io` module to your project parsing XML boils down to:

```tut:book
import pl.msitko.xml.parsing.XmlParser

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
      
import pl.msitko.xml.parsing.ParserConfig

implicit val cfg = ParserConfig.Default.copy(replaceEntityReferences = true)

XmlParser.parse(input)
```

When parsed, element `p` will have just one child:

```Text("abc This <em>is</em> an entity. def")```
