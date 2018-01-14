
import stainless.lang._
import stainless.annotation._
import stainless.collection._

object kv {

  sealed abstract class Label
  object Label {
    case class Get(key: String) extends Label
    case class Put(key: String, value: String) extends Label
  }

  sealed abstract class Op
  case class Pure(value: Option[String]) extends Op
  case class Get(key: String, next: Option[String] => Op) extends Op
  case class Put(key: String, value: String, next: () => Op) extends Op

  def get(key: String)(next: Option[String] => Op): Op = Get(key, next)
  def put(key: String, value: String)(next: () => Op): Op = Put(key, value, next)
  def pure(value: Option[String]): Op = Pure(value)

  // def interpret(op: Op)(kv: Map[String, String], trace: List[Label]): (Option[String], List[Label]) = op match {
  //   case Get(key, next)        => interpret(next(kv.get(key)))(kv, Label.Get(key) :: trace)
  //   case Put(key, value, next) => interpret(next())(kv.updated(key, value), Label.Put(key, value) :: trace)
  //   case Pure(value)           => (value, trace)
  // }

  def interpret(op: Op)(kv: Map[String, String], trace: List[Label], n: BigInt): (Option[String], List[Label]) = {
    require(n >= 0)
    decreases(n)

    op match {
      case Pure(value) =>
        (value, trace)

      case Get(key, next) if n > 0 =>
        interpret(next(kv.get(key)))(kv, Label.Get(key) :: trace, n - 1)

      case Put(key, value, next) if n > 0 =>
        interpret(next())(kv.updated(key, value), Label.Put(key, value) :: trace, n - 1)

      case _ =>
        (None(), trace)
    }
  }

  def insert(kvs: List[(String, String)])(after: Op): Op = kvs match {
    case Nil() => after
    case Cons((k, v), rest) => put(k, v) { () => insert(rest)(after) }
  }

  val xs = List("foo" -> "bar", "toto" -> "tata")
  val program = insert(xs) {
    get("foo") { foo =>
      pure(foo)
    }
  }

  def lemma(map: Map[String, String], init: List[Label]) = {
    val (res, trace) = interpret(program)(map, init, 10)

    res == Some("bar") &&
    trace.take(3) == List(
      Label.Get("foo"),
      Label.Put("toto", "tata"),
      Label.Put("foo", "bar")
    )
  } holds

}
