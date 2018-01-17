
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

  case class Context(bindings: List[(String, Expr)]) {
    def apply(name: String): Option[Expr] = {
      bindings.find(_._1 == name).map(_._2)
    }
  }

  // case class Context(bindings: Map[String, Expr]) {
  //   def apply(name: String): Option[Expr] = {
  //     bindings.get(name)
  //   }
  // }

  implicit val state = Random.newState

  @extern
  def random(max: Int): Int = {
    Random.nextInt(max)
  }

  case class Error(msg: String)
  def interpret(expr: Expr, ctx: Context): Either[Error, Int] = {
    expr match {
      case Num(value) => Right(value)

      case Var(name)  => ctx(name) match { 
        case None()      => Left(Error("Unbound variable: " + name))
        case Some(value) => interpret(value, ctx)
      }

      case Add(l, r)  => for {
        le <- interpret(l, ctx)
        re <- interpret(r, ctx)
      } yield le + re

      case Mul(l, r)  => for {
        le <- interpret(l, ctx)
        re <- interpret(r, ctx)
      } yield le * re

      case Rand(max) =>
        interpret(max, ctx).map(random(_))
    }
  }

  val program: Expr = Mul(Num(10), Add(Var("x"), Rand(Num(42))))

  @symeval
  def compiled(x: Int): Int = {
    interpret(program, Context(List("x" -> Num(x)))).get // 10 * (ctx("x") + random(42))
  }

  @symeval
  def test(y: Int) = {
    val ctx = Context(List("y" -> Num(y)))
    interpret(program, ctx)
  } ensuring { _.isLeft }

}

