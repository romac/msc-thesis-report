
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

class Wrapper(var behavior: Behavior) extends actor.Actor with actor.ActorLogging {

  implicit val ctx = ActorContext(self, context)

  def receive = {
    case msg: Msg =>
      log.info(s"$behavior: $msg")
      behavior = behavior.processMsg(msg)
    case _ => ()
  }

}

abstract class ActorSystem(val name: String) {

  lazy val system = actor.ActorSystem(name)

  def spawn(behavior: Behavior, name: String): actor.ActorRef = {
    system.actorOf(actor.Props(new Wrapper(behavior)), name = name)
  }

  def run(): Unit

}

}
