
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
      case (acc, (k, v)) if !acc.contains(k) => acc.updated(k, v)
      case (acc, _) => acc
    }
  }

  val xs = List("a" -> 1, "b" -> 2)

  @symeval
  def test(map: Map[String, Int]): (Int, Map[String, Int]) = {
    require(!map.contains("a") && map.contains("b"))
    val b   = map("b")
    val res = insert(xs, map)
    (b, res)
  } ensuring { bres =>
    val (b, res) = bres
    res("a") == 1 && res("b") == b
  }

}

