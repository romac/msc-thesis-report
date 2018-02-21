import stainless.lang._
import stainless.collection._
import stainless.annotation._

object rec {

  def f(ys: List[BigInt]): BigInt = ys match {
    case Nil() => 0
    case Cons(x, xs) => x + f(xs)
  }

  @symeval
  def shouldUnfold(xs: List[BigInt]) = {
    val ys: List[BigInt] = Cons(BigInt(1), Cons(BigInt(2), xs))
    f(ys)
  }

  def a(ys: List[BigInt]): BigInt = ys match {
    case Nil() => 0
    case Cons(x, xs) => x + b(xs)
  }

  def b(ys: List[BigInt]): BigInt = ys match {
    case Nil() => 0
    case Cons(x, xs) => x - a(xs)
  }

  @symeval
  def mutualRec(xs: List[BigInt]) = {
    val ys: List[BigInt] = Cons(BigInt(1), Cons(2, Cons(3, xs)))
    a(ys)
  }

}
