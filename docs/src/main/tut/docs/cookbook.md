---
layout: docs
title:  "Cookbook"
position: 3
---

## Cookbook

This chapter provides examples of common operations you may find useful when traversing and modifying XML trees.

All examples presented here should compile fine with following imports, if some additional import is needed it will be
covered in example code:

```tut:silent
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter
import pl.msitko.xml.dsl._
```

### How to perform a few transformations on the same XML document

In most of examples in that documentation only one transformation to XML document was done. But since transformations
are plain function `XmlDocument => XmlDocument` you can compose them with `andThen` combinator. Example:

```tut:silent
val input =
  s"""<a>
     |  <c1>
     |    <f someKey="oldValue">item1</f>
     |    <f>item2</f>
     |  </c1>
     |</a>""".stripMargin
     
val parsed = XmlParser.parse(input).right.get

val modifyAttr = (root \ "c1" \ "f").attr("someKey").set("newValue")
val modifyText = (root \ "c1" \ "f").hasTextOnly.modify(_.toUpperCase)
```

`modifyAttr` is setting attribute value while `modifyText` switch text of `f` element to uppercase. We can compose
those two transformation with `andThen` and apply composed transformation to XML document:

```tut:silent
val modify = modifyAttr andThen modifyText

val modifiedDoc = modify(parsed)
```

The result:

```tut:book
XmlPrinter.print(modifiedDoc)
```

### How to access n-th item of current focus

In the following XML you're interested only in the second `<f>` element (i.e. one containing `item2`):

```tut:silent
val input =
  s"""<a>
     |  <c1>
     |    <f>item1</f>
     |    <f>item2</f>
     |  </c1>
     |</a>""".stripMargin
     
val parsed = XmlParser.parse(input).right.get
```

You can access it using `.index(n: Int)` combinator. Here's the code:

```tut:silent
// traversal is focused on both <f> elements
val traversal = (root \ "c1" \ "f")

// and now we limit our focus just to the second one
val optional = traversal.index(1)
val res = optional.hasTextOnly.modify(_.toUpperCase)(parsed)
```

```tut:book
XmlPrinter.print(res)
```

`index(n: Int)` result type is `Optional` which means that we limited our focus to just one element.
Crucial observation here is that we have not created a new focus (e.g. one going deeper) but just chosen
one item of current focus.

### How to deepen focus to n-th elements of current focus

In the following XML you're interested only in the second `<f>` element (i.e. one containing `item2`):

```tut:silent
val input =
  s"""<?xml version="1.0" encoding="UTF-8"?>
     |<a>
     |  <f></f>
     |  <f>
     |    <h>abc</h>
     |    <i>toReplace</i>
     |  </f>
     |  <f>
     |    <h>abc</h>
     |    <i>toReplace</i>
     |  </f>
     |</a>""".stripMargin
     
val parsed = XmlParser.parse(input).right.get
```

You can access it using `.elementAt(n: Int)`. Here's the code:

```tut:silent
// traversal is focused on all three <f> elements
val traversal = (root \ "f")

// and now for each of those element we try to focus on its second child
val secondChildren = traversal.elementAt(1)
val res = secondChildren.hasTextOnly.modify(_.toUpperCase)(parsed)
```

```tut:book
XmlPrinter.print(res)
```

### Transformation in scope of whole document

In previous examples we were always performing transformations on given path. In case when you need to do some modification
in scope of whole document `monocle.function.Plated` may be useful. `xml-lens` defined instances of `Plated` for its types in
`pl.msitko.xml.optics.OpticsInstances`.

For example, to change all `Text` nodes to uppercase you can use following code:

```tut:silent
import monocle.function.Plated
import pl.msitko.xml.optics.OpticsInstances._
import pl.msitko.xml.entities.LabeledElement
import pl.msitko.xml.optics.LabeledElementOptics
import pl.msitko.xml.optics.TextOptics
import pl.msitko.xml.optics.XmlDocumentOptics.rootLens

val input = """<a>
               |  <b>some text</b>
               |  <c>
               |    <d>another text</d>
               |  </c>
               |</a>""".stripMargin

val parsed = XmlParser.parse(input).right.get

val transformation = Plated.transform[LabeledElement]{
  LabeledElementOptics.allTexts.composeIso(TextOptics.textIso).modify(_.toUpperCase)
}_
val res = rootLens.modify(transformation)(parsed)
```

```tut:book
XmlPrinter.print(res)
```
