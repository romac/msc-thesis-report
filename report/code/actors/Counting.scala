
package actors

import stainless.lang._
import stainless.proof._
import stainless.collection._
import stainless.annotation._

import scala.language.postfixOps

object counting {

  case class Primary() extends ActorRef("primary")
  case class Backup() extends ActorRef("backup")

  case class PrimBehav(counter: Counter) extends Behavior {

    override
    def processMsg(msg: Msg)(implicit ctx: ActorContext): Behavior = msg match {
      case Inc =>
        Backup() ! Deliver(counter.increment)
        PrimBehav(counter.increment)

      case _ => Behavior.same
    }
  }

  case class BackBehav(counter: Counter) extends Behavior {

    override
    def processMsg(msg: Msg)(implicit ctx: ActorContext): Behavior = msg match {
      case Deliver(c) =>
        BackBehav(c)

      case _ => Behavior.same
    }


  }

  case object Inc extends Msg
  case class Deliver(c: Counter) extends Msg

  case class Counter(value: BigInt) {
    require(value >= 0)

    @inline
    def increment: Counter =
      Counter(value + 1)

    @inline
    def <=(that: Counter): Boolean = {
      this.value <= that.value
    }
  }

  def isSorted(list: List[Msg]): Boolean = list match {
    case Nil() => true
    case Cons(Deliver(_), Nil()) => true
    case Cons(Deliver(Counter(a)), rest@Cons(Deliver(Counter(b)), xs)) => a < b && isSorted(rest)
    case _ => false // we also reject if the list contains other messages than Deliver
  }

  def validBehaviors(s: ActorSystem): Boolean = {
    s.behaviors(Primary()).isInstanceOf[PrimBehav] &&
    s.behaviors(Backup()).isInstanceOf[BackBehav]
  }

  def validRef(ref: ActorRef): Boolean = {
    ref == Primary() || ref == Backup()
  }

  def invariant(s: ActorSystem): Boolean = {
    forall((ref: ActorRef) => validRef(ref)) &&
    validBehaviors(s) &&
    s.inboxes(Primary() -> Primary()).isEmpty &&
    s.inboxes(Backup() -> Backup()).isEmpty &&
    s.inboxes(Backup() -> Primary()).isEmpty && {

      val PrimBehav(p) = s.behaviors(Primary())
      val BackBehav(b) = s.behaviors(Backup())
      val bInbox = s.inboxes(Primary() -> Backup())

      p.value >= b.value && isSorted(bInbox) && bInbox.forall {
        case Deliver(Counter(i)) => p.value >= i
        case _ => true
      }
    }
  }

  def theorem(s: ActorSystem, from: ActorRef, to: ActorRef): Boolean = {
    require(invariant(s) && validRef(from) && validRef(to))

    val newSystem = s.step(from, to)
    assert(lemma(s))
    invariant(newSystem)
  } holds

  def lemma_sameBehaviors(s: ActorSystem, from: ActorRef, to: ActorRef): Boolean = {
    require(invariant(s) && validRef(from) && validRef(to))

    assert(validBehaviors(s.step(Primary(), Backup())))
    validBehaviors(s.step(from, to))
  } holds

  def lemma(s: ActorSystem): Boolean = {
    require(invariant(s))

    s.inboxes(Primary() -> Backup()) match {
      case Nil() => s.step(Primary(), Backup()) == s
      case Cons(Deliver(c), rest) =>
        assert(lemma_sameBehaviors(s, Primary(), Backup()))
        val BackBehav(b) = s.step(Primary(), Backup()).behaviors(Backup())
        b.value == c.value
    }
  } holds

}
