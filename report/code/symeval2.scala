
import stainless.lang._
import stainless.annotation._
import stainless.collection._

object kv {

  val xs = List("a" -> 1, "b" -> 2, "c" -> 3, "d" -> 4)

  def test(map: Map[String, Int]) = {
    val res = xs.foldLeft(map) {
      case (acc, (k, v)) => acc.updated(k, v)
    }

    // res.get("a") == Some(1) &&
    // res.get("b") == Some(2) &&
    // res.get("c") == Some(3) &&
    res.get("d") == Some(4)
  }.holds

}
