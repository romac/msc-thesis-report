
import stainless.lang._
import stainless.collection._
import stainless.annotation._
import stainless.util.Random

object comp {

  sealed trait Expr
  case class Var(name: String)     extends Expr
  case class Num(value: Int)       extends Expr
  case class Add(l: Expr, r: Expr) extends Expr
  case class Mul(l: Expr, r: Expr) extends Expr
  case class Rand(max: Expr)       extends Expr

  case class Context(bindings: List[(String, Int)]) {
    def apply(name: String): Option[Int] = {
      bindings.find(_._1 == name).map(_._2)
    }
  }

  implicit val state = Random.newState

  @extern
  def random(max: Int): Int = {
    Random.nextInt(max)
  }

  def interpret(expr: Expr, ctx: Context): Int = {
    expr match {
      case Num(value) => value
      case Var(name)  => ctx(name).getOrElse(-42)
      case Add(l, r)  => interpret(l, ctx) + interpret(r, ctx)
      case Mul(l, r)  => interpret(l, ctx) * interpret(r, ctx)
      case Rand(max)  => random(interpret(max, ctx))
    }
  }

  val program: Expr = Mul(Num(10), Add(Var("x"), Rand(Num(42))))

  @symeval
  def compiled(x: Int): Int = {
    interpret(program, Context(List("x" -> x)))
  }

  def test = {
    compiled(1) == 4
  } holds

  // def compiled(ctx: Map[String, Int]): Int = {
  //   interpret(program, ctx)
  // } ensuring { _ == -1 }

  // def result(ctx: Map[String, Int]): Int = {
  //   10 * (ctx("x") + 2)
  // }

}

