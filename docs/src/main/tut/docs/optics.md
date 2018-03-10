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
`Traversal` will be particularly helpful. If you don't now where to start educating about Optics you may find
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

### Plated

### Resources on Optics

## DSL (high-level API)

### Purpose of DSL

### How to use DSL

#### Exemplary usage <a name="exemplary-usage-dsl"></a>

We'll rewrite the same transformation as described [here](#exemplary-usage-optics-api), this time with DSL.

```tut:silent
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter
import pl.msitko.xml.syntax.OpticsBuilder.root // TODO: change it in code to sth more user friendly


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