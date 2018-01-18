
import stainless.lang._
import stainless.annotation._
import stainless.linear._

object linlist {

  case class -*>[A, B](f: Linear[A] => B) {
    def apply(x: Linear[A]): B = f(x)
  }

  implicit def toLinFun[A, B](f: Linear[A] => B): A -*> B = -*>(f)

  @linear
  sealed abstract class LList[A] {

    def map[B](f: A -*> B): Linear[LList[B]] = this match {
      case LNil() =>
        val res: Linear[LList[B]] = LList.empty[B]
        res

      case LCons(h, t) =>
        val hb: Linear[B]         = f(h)
        val tb: Linear[LList[B]]  = t.map(f)
        val res: Linear[LList[B]] = linearize[LList[B]](LCons(hb, tb))
        res
    }

    @inline
    def ::(x: Linear[A]): Linear[LList[A]] = {
      linearize[LList[A]](LCons(x, this))
    }
  }

  object LList {
    def empty[A]: Linear[LList[A]] = linearize[LList[A]](LNil[A]())
  }

  // type LinList[A] = Linear[LList[A]]

  // @linear
  // sealed abstract class LList[A] {
  //   def map[B](f: A -*> B): LinList[B] = this match {
  //     case LNil() =>
  //       val res: LinList[B] = LNil[B]().asInstanceOf[LList[B]]
  //       res

  //     case LCons(h, t) =>
  //       val hb: Linear[B]         = f(h)
  //       val tb: LinList[B]  = t.map(f)
  //       val res: LinList[B] = LCons[B](hb, tb).asInstanceOf[LList[B]]
  //       res
  //   }
  // }

  case class LNil[A]() extends LList[A]
  case class LCons[A](head: Linear[A], tail: Linear[LList[A]]) extends LList[A]

  def ok(xs: Linear[LList[Int]]): Linear[LList[Int]] = {
    xs.map((x: Linear[Int]) => x + 1)
  } ensuring { res => test(2) }

  def bad(xs: Linear[LList[Int]]): Linear[LList[Int]] = {
    val f = (x: Linear[Int]) => x + x + 1
    xs.map(f)
  } ensuring { res => test(2) }

  def test(x: Int) = x > 0

}

