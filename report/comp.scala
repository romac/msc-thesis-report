
import stainless.lang._
import stainless.collection._
import stainless.annotation._

object comp {

  sealed trait Expr
  case class Var(name: String)     extends Expr
  case class Num(value: Int)       extends Expr
  case class Add(l: Expr, r: Expr) extends Expr
  case class Mul(l: Expr, r: Expr) extends Expr

  case class Context(bindings: List[(String, Int)]) {
    def apply(name: String): Int = bindings.find(_._1 == name).map(_._2).get
  }

  def interpret(expr: Expr, ctx: Context): Int = expr match {
    case Num(value) => value
    case Var(name)  => ctx(name)
    case Add(l, r)  => interpret(l, ctx) + interpret(r, ctx)
    case Mul(l, r)  => interpret(l, ctx) * interpret(r, ctx)
  }

  val program: Expr = Mul(Num(10), Add(Var("x"), Num(2)))

  def compiled(x: Int): Int = {
    interpret(program, Context(List("x" -> x)))
  } ensuring { _ == -1 }

  // def compiled(ctx: Map[String, Int]): Int = {
  //   interpret(program, ctx)
  // } ensuring { _ == -1 }

  // def result(ctx: Map[String, Int]): Int = {
  //   10 * (ctx("x") + 2)
  // }

}

