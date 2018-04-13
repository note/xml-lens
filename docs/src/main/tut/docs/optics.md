---
layout: docs
title:  "Optics API"
position: 2
---

The most interesting part of `xml-lens` is API for XML transformations. It's divided into two layers:
Optics API (also called low-level API) and DSL (also called high-level API).

## Optics API (low-level API)

### Purpose of Optics API

Optics API is the heart of `xml-lens`. Optics offers way of constructing immutable, principled APIs on any ADTs. 
Therefore, they promise a good solution for operating on XML. [circe-optics](https://github.com/circe/circe/blob/master/docs/src/main/tut/optics.md)
was also an inspiration for such solution.

The main purpose of Optics API is to provide set of lawful Optics to modify and traverse `xml-lens` AST.
The focus of that API is more on completeness rather than on ease of use.

### When to use Optics API

You should use Optics API if high-level API does not provide functionality you need. Otherwise use high-level API
which is easier to use.

When utilizing Optics API you will operate directly on `Monocle` types. There is some theory behind them and therefore 
some basic knowledge of it is neccessary to comfortably use this kind of API. Understanding what is `Lens`, `Prism` and 
`Traversal` will be particularly helpful. If you don't know where to start educating about Optics you may find
section [resources](#resources-on-optics) relevant.

### How to use Optics API

In order to explain how to use Optics API it will be presented some motivational example, followed by 
analysis how it works and suggestions how to explore Optics API by yourself.

#### Exemplary usage <a name="exemplary-usage-optics-api"></a>

In this example we will modify all text nodes in path `b/c`:

```tut:silent
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter
import pl.msitko.xml.optics.ElementOptics._
import pl.msitko.xml.optics.LabeledElementOptics._
import pl.msitko.xml.optics.XmlDocumentOptics._

val exampleXml = """<?xml version="1.0" encoding="UTF-8"?>
<a>
   <b>
      <c>item1</c>
      <d>item2</d>
   </b>
   <b>
      <c>item1</c>
      <e>item2</e>
      <c>item3</c>
   </b>
</a>"""

val parsed = XmlParser.parse(exampleXml).right.get

val traversal = rootLens.composeTraversal(deep("b").composeTraversal(deeper("c")).composeOptional(hasTextOnly))

val res = traversal.modify(_.toUpperCase)(parsed)
```

```tut:book
XmlPrinter.print(res)
```

You can find DSL equivalent [here](#exemplary-usage-dsl). The result is as expected, now let's analyze API more deeply.

Usually you'll start constructing any transformation with:

```tut:book
import pl.msitko.xml.optics.XmlDocumentOptics

XmlDocumentOptics.rootLens
```

which as you see is simply `monocle.Lens`. Then to compose it with other optics you can use its `compose...` methods.

You man wonder how can you know what to import from `import pl.msitko.xml.optics`. The general rule is that optics
which source is of type `A` are defined in `pl.msitko.xml.optics.A`

**Example:** You started defining definition with `rootLens` of type `Lens[XmlDocument, LabeledElement]`. Now you need to
compose it with optics which source type is `LabeledElement`. Then you need to `import pl.msitko.xml.optics.LabeledElement`.

Unfortunately `xml-lens` cannot define package object that would define all optics and would let you to include all optics
with one import. The reason for that is that some names of optics are the same between different source type, e.g.
`LabeledElementOptics.children` and `ElementOptics.children`.

## DSL (high-level API)

### Purpose of DSL

The goal of DSL is to provide a set of convenient combinators for the most common operations.
Those combinators are implemented on top of Optics API.

### How to use DSL

Firstly you need to `import pl.msitko.xml.dsl._`. It will bring `root` into scope. `root` represents
the root element of XML document. Therefore, it serves as starting point for defining any transformations.

#### Exemplary usage <a name="exemplary-usage-dsl"></a>

We will rewrite the same transformation as described [here](#exemplary-usage-optics-api), this time with DSL.

```tut:silent
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter
import pl.msitko.xml.dsl._


val exampleXml = """<?xml version="1.0" encoding="UTF-8"?>
<a>
   <b>
      <c>item1</c>
      <d>item2</d>
   </b>
   <b>
      <c>item1</c>
      <e>item2</e>
      <c>item3</c>
   </b>
</a>"""

val parsed = XmlParser.parse(exampleXml).right.get

val transformation = (root \ "b" \ "c").hasTextOnly.modify(_.toUpperCase)

val res = transformation(parsed)
```

```tut:book
XmlPrinter.print(res)
```

### Resources on Optics

#### Articles

* A presentation of the most important types of optics: [link](https://blog.scalac.io/optics-beyond-lenses-with-monocle.html)
* Monocle [documentation](http://julien-truffaut.github.io/Monocle/)
* [List of references](http://julien-truffaut.github.io/Monocle/learning_resources.html) from Monocle documentation
* Scala Exercises [page](https://www.scala-exercises.org/monocle/iso)

#### Talks

* Ilan Godik's [talk](https://www.youtube.com/watch?v=NvCcNM2vp3k) - great introductory talk into Optics in Scala using Monocle
by one of its maintainers. Short and does not require any specific knowledge upfront. Also introduces [Van Laarhoven Lenses](https://youtu.be/NvCcNM2vp3k?t=18m40s)
* Julien Truffaut's [talk](https://www.youtube.com/watch?v=6nyGVgGEKdA) - Julien is an author of Monocle, in this talk he
provides great overview and intuitions about various types of Optics
* another [talk](https://skillsmatter.com/skillscasts/8969-jsonpath-type-safe-query-dsl-using-optics) by Julien Truffaut - this
one is about `JsonPath` - concept already mentioned in this article in [section](#circe) covering `circe-optics`
* Brian McKenna's [talk](https://www.youtube.com/watch?v=H01dw-BMmlE) - Brian goes through Optics libraries in a few different
languages: PureScript, Haskell, Scala and Java. Mentions nice examples of applications including [representing](https://youtu.be/H01dw-BMmlE?t=4m21s)
web pages as Optics which allows to navigate between state and UI in [Halogen](https://github.com/slamdata/purescript-halogen), [working with Kinesis records](https://youtu.be/H01dw-BMmlE?t=6m49s)
in Haskell, [handling errors](https://youtu.be/H01dw-BMmlE?t=13m20s) with Prisms in Scala