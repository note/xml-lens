package pl.msitko.xml.printing

import pl.msitko.xml.entities.ResolvedName

private [printing] trait Resolver {
  def resolve(name: ResolvedName): String =
    if (name.hasPrefix) {
      name.prefix + ":" + name.localName
    } else {
      name.localName
    }
}
