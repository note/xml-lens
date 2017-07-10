XML Optics library for Scala. Documentation available here: https://note.github.io/xml-lens/

[![Build Status](https://api.travis-ci.org/note/xml-lens.svg)](https://travis-ci.org/note/xml-lens)
[![codecov](https://codecov.io/gh/note/xml-lens/branch/master/graph/badge.svg)](https://codecov.io/gh/note/xml-lens)


## Motivation

XML scala libraries are kind of neglected. That stands in stark contrast to JSON for which Scala has dozens of projects.
Of course, JSON is much more popular and XML at the same time is regarded as a legacy standard but still - there are many situations where you need to work with XML.

## Status of project

Library is in an early state of initial development.

## Various

#### How to generate documentation

```
sbt docs/makeMicrosite
```

After docs has been successfully generated you serve it with:

```
cd docs/target/site
jekyll serve
```

#### How to run JMH benchmark
 
Example:

```
bench/jmh:run -i 10 -wi 10 -f1 -t1 -prof gc .*Roundtrip*.
```