package pl.msitko.xml.printing

// mostly not to make io module depending on cats/scalaz
private [printing] trait InternalMonoid [T] {
  def combine(a: T, b: String): T
  def combine(a: T, ch: Char): T
  def zero: T
}

private [printing] object InternalMonoid {
  def apply[T : InternalMonoid]: InternalMonoid[T] = implicitly[InternalMonoid[T]]

  implicit val stringMonoidInstance = new InternalMonoid[String] {
    override def combine(a: String, b: String) = a + b
    override def combine(a: String, ch: Char) = a + ch

    override def zero = ""
  }

  implicit val stringBuilderMonoidInstance = new InternalMonoid[StringBuilder] {
    override def combine(a: StringBuilder, b: String) = a.append(b)
    override def combine(a: StringBuilder, ch: Char) = a.append(ch)

    override def zero = new StringBuilder
  }
}

private [printing] object Syntax {
  implicit class InternalMonoidWithCombine[M : InternalMonoid](m: M) {
    def combine(b: String) = InternalMonoid[M].combine(m, b)
    def combine(ch: Char)  = InternalMonoid[M].combine(m, ch)
  }
}