---
layout: home
title:  "Home"
section: "home"
---

[![Build Status](https://api.travis-ci.org/note/xml-lens.svg)](https://travis-ci.org/note/xml-lens)
[![Coverage Status](https://coveralls.io/repos/github/note/xml-lens/badge.svg?branch=master)](https://coveralls.io/github/note/xml-lens?branch=master)

## About

XML Scala libraries are kind of neglected. That stands in stark contrast to JSON for which Scala has dozens of projects.
Of course, JSON is much more popular while XML is regarded a legacy standard. Still, there are many situations in which 
you need to work with XML. `xml-optics` is an attempt to make this experience as painless as possible.
  
Main focus of `xml-optics` is traversing and transforming XML trees - something `scala-xml` is not 
really good at. To make those operations easy to express the concept of `Optics` has been used. 
[Monocle](http://julien-truffaut.github.io/Monocle/) has been used as an implementation of `Optics`.

`xml-lens` is available for both Scala 2.11 and 2.12. It's cross published for both JVM and Scala.js.

## How to read this document

If you're interested what is xml-lens and when it may be useful to you go [ahead](#goals-and-non-goals-of-the-project)
with reading current page.

If you're already convinced that you want to use it start by reading [quickstart](docs/quickstart.html).

## Goals and non-goals of the project

Goals of the project:

* provide optics API for transforming XML. It should be stressed out here: while `scala-xml` provides
 API sufficient for many operations it's designed mostly with `read-transform-write` scenario in mind.
* provide easy to use API built on top of optics API
* reasonable performance (should not be much slower than e.g. `scala-xml`)
* scala.js support (differences between platform should be limited as far as it is possible)
* good documentation
* serve as experimentation field of what can be achieved with optics in regards of XML transformations.
There is nothing fancy in current implementation but there are many interesting direction for future
development.

Non-goals of the project:

* provide state of the art performance
* be DTD-aware
* be XSD Schema-aware

### Motivational example

Let's assume that you need to transform text node `a/interesting/special` in the following XML to be
formatted with upper case.

```tut:silent
val motivationalExample =
      """<?xml version="1.0" encoding="UTF-8"?>
        |<a>
        |  <boring>
        |    <special>text</special>
        |  </boring>
        |  <interesting>
        |    <special>text</special>
        |    <boring>text</boring>
        |  </interesting>
        |  <special>text</special>
        |</a>""".stripMargin
```

Instead of presenting you reference implementation in `scala-xml` first, we will start by taking
a look at solution with `xml-lens`. Such order will make it easier to understand the problem.

It will be easier to start with solution to motivational problem written with `xml-lens`.

With `xml-lens` you can solve motivational problem with:

```tut:silent
import pl.msitko.xml.parsing.XmlParser
import pl.msitko.xml.printing.XmlPrinter
import pl.msitko.xml.dsl._

val parsed = XmlParser.parse(motivationalExample).right.get

val modify = (root \ "interesting" \ "special").hasTextOnly.modify(_.toUpperCase)
val res = modify(parsed)

XmlPrinter.print(res)
```

The code is straightforward and declarative.

Actually it's quite difficult to write the same logic with `scala-xml`. There is `scala.xml.transform`
API but it focuses on global transformation and here we want to transform just specific `special` node.
You can access that specific node with `"interesting" \ "special"` syntax in `scala-xml` too but as soon
as you do that you "lost" the rest of the document - in our case we want to return the whole XML document
with remaining nodes untouched.

Therefore you end up manipulating AST directly which is very cumbersome and error-prone:

```tut:silent
import java.io.StringWriter
import scala.xml.{Elem, Text, XML}

def transform(el: Elem): Elem = {
    if(el.child.size == 1) {
      val replaceWith = el.child.head match {
        case t: Text =>
          Text(t.text.toUpperCase)
        case a => a
      }
      el.copy(child = List(replaceWith))
    } else {
      el
    }
  }

val xml = XML.loadString(motivationalExample)

val transformed = xml.map {
  case el: Elem if el.label == "a" =>
    el.copy(child = el.child.flatMap {
      case el: Elem if el.label == "interesting" =>
        el.copy(child = el.child.flatMap {
          case el: Elem if el.label == "special" =>
            transform(el)
          case a => a
        })
      case a => a
    })
  case a => a
}

val writer = new StringWriter

XML.write(writer, transformed.head, "UTF-8", true, null)
writer.toString
```

As you can see it is really convoluted way of transforming XML. Of course, that code has a lot of
repetition and can probably be written more concisely with additional custom abstractions but the
point here is that `scala-xml` does not provide anything outside of the box.


## Peculiarities

### Ignoring comments and processing instructions following root element

According to [specs](https://www.w3.org/TR/xml/#sec-well-formed) XML document is:

```
document  ::=  prolog element Misc*
```

But for sake of simplicity xml-lens defines it rather as:

```
document  ::=  prolog element
```

That means that comments and processing instructions that are placed after the root element cannot be
expressed using `xml-lens` AST. Mind that it does not apply to comments and processing instructions which
are places outside of root element but before it. Those items can be expressed in terms of `xml-lens` AST
as part of `Prolog`.

## License

All code is available to you under the MIT license, available [here](https://github.com/note/xml-lens/blob/master/LICENSE).


## Acknowledgements

Early development of `xml-lens` was funded by [scalac](https://scalac.io/).