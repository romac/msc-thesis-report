
import stainless.lang._
import stainless.collection._

object term {

  def nonterminating(xs: List[BigInt]): List[BigInt] = {
    if (xs.isEmpty) Nil() else nonterminating(Cons(xs.head, xs))
  }

  def infinite = {
    nonterminating(List(1)) == Nil[BigInt]()
  } holds

}

