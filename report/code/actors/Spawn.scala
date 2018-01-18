
package actors

import stainless.lang._
import stainless.proof._
import stainless.collection._
import stainless.annotation._

import scala.language.postfixOps

object spawn {

  case class Primary() extends ActorRef("primary")

  case class BeforeB() extends Behavior {
    def processMsg(msg: Msg)(implicit ctx: ActorContext): Behavior = msg match {
      case Spawn() =>
        val child = ctx.spawn(ChildB(), "child")
        AfterB(child)
    }
  }

  case class AfterB(child: ActorRef) extends Behavior {
    def processMsg(msg: Msg)(implicit ctx: ActorContext): Behavior = msg match {
      case _ => Behavior.same
    }
  }

  case class ChildB() extends Behavior {
    def processMsg(msg: Msg)(implicit ctx: ActorContext): Behavior = msg match {
      case _ => Behavior.same
    }
  }

  case class Spawn() extends Msg

  val childRef = Child("child", Primary())

  def invariant(s: ActorSystem): Boolean = {
    s.behaviors(Primary()) match {
      case BeforeB() =>
        s.isStopped(childRef)

      case AfterB(child) =>
        child == childRef &&
        s.behaviors(childRef) == ChildB()

      case _ => false
    }
  }

  def theorem(s: ActorSystem, from: ActorRef, to: ActorRef): Boolean = {
    require(invariant(s))
    invariant(s.step(from, to))
  } holds

}
