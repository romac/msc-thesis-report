
import stainless.lang._
import stainless.proof._
import stainless.collection._
import stainless.annotation._

import scala.language.postfixOps

import akka.actor

package object akkashim {

type ActorRef = actor.ActorRef

abstract class Msg

case class ActorContext(self: actor.ActorRef, ctx: actor.ActorContext) {
  def spawn(behavior: Behavior, name: String): actor.ActorRef = {
    ctx.actorOf(actor.Props(new Wrapper(behavior)), name = name)
  }
}

abstract class Behavior {
  def processMsg(msg: Msg)(implicit ctx: ActorContext): Behavior

  @inline
  implicit def sameBehavior: Behavior = this
}


object Behavior {
  case class Stopped() extends Behavior {
    def processMsg(msg: Msg)(implicit ctx: ActorContext): Behavior = {
      Behavior.same
    }
  }

  @inline
  def same(implicit behavior: Behavior): Behavior = behavior

  @inline
  def stopped: Stopped = Stopped()
}

