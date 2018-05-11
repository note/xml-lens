XML Optics library for Scala. Documentation available here: https://note.github.io/xml-lens/

[![Build Status](https://api.travis-ci.org/note/xml-lens.svg)](https://travis-ci.org/note/xml-lens)
[![Coverage Status](https://coveralls.io/repos/github/note/xml-lens/badge.svg?branch=master)](https://coveralls.io/github/note/xml-lens?branch=master)


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

After docs has been successfully generated you can serve it with:

```
cd docs/target/site
jekyll serve
```

#### Pushing documentation to github page

You can push generated documentation with:

```
docs/ghpagesPushSite
```

Mind that you have to have access to push to repository defined in `build.sbt` to make above snippet work.

#### How to run JMH benchmark
 
Example:

```
bench/jmh:run -i 10 -wi 10 -f1 -t1 -prof gc .*Roundtrip*.
```

## License

All code is available to you under the MIT license, available [here](https://github.com/note/xml-lens/blob/master/LICENSE).


## Acknowledgements

Early development of `xml-lens` was funded by [scalac](https://scalac.io/).
