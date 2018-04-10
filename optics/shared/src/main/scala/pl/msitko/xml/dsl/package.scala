package pl.msitko.xml

package object dsl extends ToDocumentOps with ToNodeOps {
  def root = new RootBuilder
}
