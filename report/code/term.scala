
import stainless.lang._
import stainless.annotation._
import stainless.collection._

object term {

  def f(xs: List[BigInt]): List[BigInt] = {
    f(xs)
  }

  def g(xs: List[BigInt]): List[BigInt] = {
    if (xs.isEmpty) Nil() else g(Cons(xs.head, xs))
  }

  def h(xs: List[BigInt]): List[BigInt] = {
    require(xs.nonEmpty)
    if (xs.isEmpty) {
      h(xs)
    } else {
      xs
    }
  }

  def i(a: Boolean, n: BigInt): Boolean = {
    require(n != 0)
    if (n == 0) i(a, 0) else a
  }

  @symeval
  def bad0 = {
    f(List(1)) == Nil[BigInt]()
  } holds

  @symeval
  def bad1 = {
    g(List(1)) == Nil[BigInt]()
  } holds

  @symeval
  def ok0 = {
    h(Nil()) == Nil[BigInt]()
  } holds

  @symeval
  def ok1 = {
    i(true, 0) == true
  } holds

  @symeval
  def test(a: List[BigInt]) = {
    require(a.nonEmpty)
    lemma(a) != None[BigInt]()
  } holds

  def lemma(a: List[BigInt]): Option[BigInt] = {
    a match {
      case Cons(x, xs) => lemma(xs).map(_ + x)
      case Nil() => Some(0)
    }
  }

}

