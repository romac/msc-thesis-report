
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

  def trick(n: Int, xs: List[Int]): Int = {
    if (n > 0) {
      xs match {
        case Nil() => n
        case Cons(x, xs) => x + trick(n, xs)
      }
    } else {
      0
    }
  }

  @symeval
  def test1(n: Int, xs: List[Int]) = {
    require(n > 0)
    trick(n, xs)
  }

  @symeval
  def test2(n: Int, xs: List[Int]) = {
    require(n > 0)
    trick(n, List(1, 2, 3))
  }

  @symeval
  def test3(n: Int, xs: List[Int]) = {
    require(n > 0)
    trick(n, Cons(1, Cons(2, Cons(3, xs))))
  }

}

