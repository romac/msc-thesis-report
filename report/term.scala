
import stainless.lang._
import stainless.collection._

object term {

  def f(xs: List[BigInt]): List[BigInt] = {
    if (xs.isEmpty) Nil() else f(Cons(xs.head, xs))
  }

  def g(xs: List[BigInt]): List[BigInt] = {
    g(xs)
  }

  def bad1 = {
    f(List(1)) == Nil[BigInt]()
  } holds

  def ok1 = {
    g(List(1)) == Nil[BigInt]()
  } holds

}

