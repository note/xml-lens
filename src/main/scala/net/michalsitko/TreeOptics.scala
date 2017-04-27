package net.michalsitko

import monocle.{Iso, Lens, POptional, Prism}

object TreeOptics extends ExprOptics {
  def main(args: Array[String]): Unit = {
    println("bazinga")

    val e = BinOp("+", BinOp("*", Var("a"), Var("b")), BinOp("+", Var("c"), Var("d")))
    val res = modifyTopBinOpToMul(e)

    println("result: " + res)
  }

  private def modifyTopBinOpToMul(e: Expr): Expr = {
    val op: POptional[Expr, Expr, String, String] = binOp.composeLens(binOpStr)
    op.set("changed")(e)
  }
}

sealed abstract class Expr
case class Var(name: String) extends Expr
case class Number(num: Double) extends Expr
case class UnOp(operator: String, arg: Expr) extends Expr
case class BinOp(operator: String,
                 left: Expr, right: Expr) extends Expr


trait ExprOptics {
  lazy val binOp = Prism.partial[Expr, BinOp] { case op: BinOp => op }(identity)
  lazy val binOpStr = Lens[BinOp, String](_.operator)(str => op => op.copy(operator = str))
}

object ExprOptics extends ExprOptics
