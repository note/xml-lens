---
layout: docs
title:  "Quickstart"
position: 1
---

## Quickstart

In this section there is a simple case presented in copy-paste ready form. In the next section it's
explained more deeply.

Add following lines to your `build.sbt`:

```
libraryDependencies ++= Seq(
	"pl.msitko" %% "xml-lens-io"     % "0.1.0-RC6",
	"pl.msitko" %% "xml-lens-optics" % "0.1.0-RC6"
)
```

In scala-js you basically need to replace `%%` with `%%%`. Because scala-js builds tend to be tricky
due to various bundlers, there's a [sample project](https://github.com/note/xml-lens-scala-js-example)
available.

Then in your Scala code you can:

```tut:silent
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter
import pl.msitko.xml.dsl._

// some XML input
val input =
    """<?xml version="1.0" encoding="UTF-8"?>
      |<a>
      |  <e>item</e>
      |  <f>item</f>
      |  <g>item</g>
      |</a>""".stripMargin

// turn `f` node to upper case - define transformation
val modify = (root \ "f").hasTextOnly.modify(_.toUpperCase)

// parse XML
val parsed = XmlParser.parse(input).right.get

// apply transformation
val res = modify(parsed)
```

Now `res` contains modified XML document. Let's print it out to verify if it's what we intended:

```tut:book
println(XmlPrinter.print(res))
```

## Quickstart - explained

The whole example is about transforming one path of XML to be in upper case.

Firstly, we define the transformation in the following way:

```tut:book
import pl.msitko.xml.dsl._

val modify = (root \ "f").hasTextOnly.modify(_.toUpperCase)
```

`root` is available thanks to `import pl.msitko.xml.dsl._`. `root` stands for root element of XML
document. Every XML document should contain exactly one root element. In case of our example `<a>`
is a root element.

Then we can navigate deeper using `\` operator. It takes a `String` argument which is a label of direct children we want to
"zoom into". There's an overloaded `\` operator which takes `NameMatcher` instead of `String` which allows you to specify
namespace. You can read more about it in section [Namespaces](docs/namespaces.html). `\` taking `String` ignores namespaces -
will match all nodes that have specified label.

Next, we narrow down our selection specifically to `f` nodes having only a text child. We do this by calling `hasTextOnly`.

After we specified what we want to modify, we can define what it should be modified to. We do this by using `modify`,
which takes a function from `String` to `String`.

Now, when we have `modify` function declared we can use it on parsed XML. The code which parses input and
run `modify` transformation on it looks like this:

```tut:silent
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter

val parsed = XmlParser.parse(input).right.get

val res = modify(parsed)
```

Finally, we can print the result back to verify it:

```tut:book
XmlPrinter.print(res)
```

### Where should I go from here

Recommended next reading is [chapter](docs/modularity.html) on modularity and [chapter](docs/optics.html)
covering Optics API. Those chapters will help you understanding how `xml-lens` API is structured.

In case of specific questions take a look at other chapters of this docs.
