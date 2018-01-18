package actors

import stainless.lang._
import stainless.collection._
import stainless.annotation._

import scala.language.postfixOps

object ActorSystem {
  def named(name: String): ActorSystem = {
    ActorSystem(
      name,
      CMap(_ => Behavior.stopped),
      CMap(_ => List())
    )
  }
}

case class ActorSystem(
  name: String,
  behaviors: CMap[ActorRef, Behavior],
  inboxes: CMap[(ActorRef, ActorRef), List[Msg]]
) {

  def run(): Unit = {}

  def step(from: ActorRef, to: ActorRef): ActorSystem = {
    inboxes(from -> to) match {
      case Nil() =>
        this

      case Cons(msg, msgs) =>
        val (newBehavior, toSend, toSpawn) = deliverMessage(to, from, msg)

        val newBehaviors = updateBehaviors(toSpawn, behaviors.updated(to, newBehavior))
        val newInboxes = updateInboxes(to, toSend, inboxes.updated(from -> to, msgs))

        ActorSystem(
          name,
          newBehaviors,
          newInboxes
       )
    }
  }

  def updateInboxes(from: ActorRef, toSend: List[Packet], inboxes: CMap[(ActorRef, ActorRef), List[Msg]]): CMap[(ActorRef, ActorRef), List[Msg]] = toSend match {
    case Nil() =>
      inboxes
    case Cons(Packet(to, msg), tms) =>
      updateInboxes(from, tms, inboxes.updated(from -> to, msg :: inboxes(from -> to)))
  }

  def updateBehaviors(toSpawn: List[(ActorRef, Behavior)], behaviors: CMap[ActorRef, Behavior]): CMap[ActorRef, Behavior] = toSpawn match {
    case Nil() =>
      behaviors
    case Cons((ref, behav), rbs) =>
      updateBehaviors(rbs, behaviors.updated(ref, behav))
  }

  def deliverMessage(to: ActorRef, from: ActorRef, msg: Msg): (Behavior, List[Packet], List[(ActorRef, Behavior)]) = {
    val behavior = behaviors(to)

    val ctx = ActorContext(to, Nil(), Nil())
    val nextBehavior = behavior.processMsg(msg)(ctx)

    (nextBehavior, ctx.toSend, ctx.toSpawn)
  }

  @inline
  def isStopped(id: ActorRef): Boolean = {
    behaviors(id) == Behavior.stopped
  }

  def send(from: ActorRef, to: ActorRef, msg: Msg): ActorSystem = {
    val inbox = msg :: inboxes(from -> to)
    ActorSystem(name, behaviors, inboxes.updated(from -> to, inbox))
  }

  def withInbox(from: ActorRef, to: ActorRef, msgs: List[Msg]): ActorSystem = {
    ActorSystem(name, behaviors, inboxes.updated(from -> to, msgs))
  }

  def spawn(ref: ActorRef, behavior: Behavior): ActorSystem = {
    ActorSystem(name, behaviors.updated(ref, behavior), inboxes)
  }

}

