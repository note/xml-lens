---
layout: docs
title:  "Cookbook"
position: 1
---

## Cookbook

This chapter provides examples of common operations you may find useful when traversing and modifying XML trees.

All examples presented here should compile fine with following imports:

```tut:silent
import net.michalsitko.xml.parsing.XmlParser
import net.michalsitko.xml.printing.XmlPrinter
import net.michalsitko.xml.syntax.OpticsBuilder._
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

### How to access n-th children of current focus

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


