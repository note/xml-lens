---
layout: home
title:  "Home"
section: "home"
---

remove me

## About

XML scala libraries are kind of neglected. That stands in stark contrast to JSON for which Scala has dozens of projects.
Of course, JSON is much more popular while XML is regarded as a legacy standard. Still, there are many situations when 
you need to work with XML. `xml-optics` is an attempt to make this experience as painless as possible.
  
Main focus of `xml-optics` is on traversing and transforming XML trees - something `scala-xml` is not 
really good at. To make those operations natural concept of `Optics` has been used.

## <a name="quick_start">Quick start</a>

```tut
import scala.concurrent.Future
val s = Future.successful("hello")
val x = 44
```

And now:

```tut
val y = x + 10
println(y)
```