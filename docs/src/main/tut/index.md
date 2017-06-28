---
layout: home
title:  "Home"
section: "home"
---

## About

XML scala libraries are kind of neglected. That stands in stark contrast to JSON for which Scala has dozens of projects.
Of course, JSON is much more popular while XML is regarded as a legacy standard. Still, there are many situations when 
you need to work with XML. `xml-optics` is an attempt to make this experience as painless as possible.
  
Main focus of `xml-optics` is on traversing and transforming XML trees - something `scala-xml` is not 
really good at. To make those operations natural concept of `Optics` has been used.

## <a name="quick_start">Quick start</a>

TODO: add lines needed in `build.sbt` as soon as library is published.

Let's say we want to transform `f` node in the following XML in such way that its text value will be converted to 
upper case:

```tut:silent
val input =
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a>
        |  <e>item</e>
        |  <f>item</f>
        |  <g>item</g>
        |</a>""".stripMargin
```

We can define transformation in the following way: 

```tut
import net.michalsitko.xml.syntax.OpticsBuilder.root

val modify = (root \ "f").hasTextOnly.modify(_.toUpperCase)
```

`root` stands for root element of XML document. Every XML document should contain exactly one root element. In case
of our example `<a>` is a root element. 

Then we can navigate deeper using `\` operator. It takes a `String` argument which is a label of direct children we want to 
"zoom into". There's an overloaded `\` operator which takes `NameMatcher` instead of `String` which allows you to specify 
namespace. You can read more about it in section [Namespaces](docs/namespaces.html). `\` taking `String` ignores namespaces - 
will match all nodes that have specified label. 

Next, we narrow down our selection specifically to `f` nodes having only a text child. We do this by calling `hasTextOnly`.

After we specified what we want to modify, we can define what it should be modified to. We do this by using `modify`,
which takes a function from `String` to `String`.

Now, when we have `modify` function declared we can use it on parsed XML. Here the full code which parse input,
modify it:

```tut:silent
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter

val parsed = XmlParser.parse(input).right.get

val res = modify(parsed)
```

Finally, we can print the result back to verify it:

```tut
XmlPrinter.print(res)
```
