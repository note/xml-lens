---
layout: docs
title:  "Modularity"
position: 2
---

## Modularity

`xml-lens` consists of three modules. They can be depicted as follows:

![xml-lens modules](../img/modules.png){:class="img-responsive"}

* `AST` - very small module containing just ADTs to represent XML document. It has no dependencies and is platform 
independent
* `optics` - module containing all `Optics` and DSL implemented in terms of those Optics. It depends on Monocle and is 
platform independent
* `io` - module containing parser and printer implementations. It is platform dependent. As of now implementations
for JVM and scala.js exist
