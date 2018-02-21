
import stainless.lang._
import stainless.annotation._
import stainless.collection._

object intro {
  def foldLeft[A, B](list: List[A], z: B)(f: (B, A) => B): B = list match {
    case Nil() => z
    case Cons(x, xs) => foldLeft(xs, f(z, x))(f)
  }

  def insert[A, B](kvs: List[(A, B)], map: Map[A, B]) = {
    foldLeft(kvs, map) {
      case (acc, (k, v)) if !acc.contains(k) =>
        acc.updated(k, v)
      case (acc, _) =>
        acc
    }
  }

  @symeval
  def test(map: Map[String, Int], x: Int, y: Int) = {
    require(!map.contains("a") && map.contains("b"))
    val xs = Cons("a" -> x, Cons("b" -> y, Nil()))
    val res = insert(xs, map)
    res("a") == x && res("b") == map("b")
  } holds

}

