
import stainless.lang._
import stainless.proof._
import stainless.collection._
import stainless.annotation._

import scala.language.postfixOps

import akkashim._

object registry {

  object MainB {
    case object Init extends Msg
  }

  case class MainB(registry: ActorRef, initialized: Boolean = false) extends Behavior {
    import MainB._

    def processMsg(msg: Msg)(implicit ctx: ActorContext): Behavior = msg match {
      case Init if !initialized =>
        registry ! RegistryB.Register(ctx.self)

        val w1 = ctx.spawn(WorkerB(false), "worker-" + 1)
        val w2 = ctx.spawn(WorkerB(false), "worker-" + 2)

        w1 ! WorkerB.Init(registry)
        w2 ! WorkerB.Init(registry)

        MainB(registry, true)

      case _ =>
        Behavior.same
    }
  }

  object WorkerB {
    case class Init(registry: ActorRef) extends Msg
  }

  case class WorkerB(registered: Boolean = false) extends Behavior {
    import WorkerB._

    def processMsg(msg: Msg)(implicit ctx: ActorContext): Behavior = msg match {
      case Init(registry) if !registered =>
        registry ! RegistryB.Register(ctx.self)
        WorkerB(true)

      case _ =>
        Behavior.same
    }
  }

  object RegistryB {
    case class Register(me: ActorRef) extends Msg
  }

  case class RegistryB(register: List[ActorRef] = Nil()) extends Behavior {
    import RegistryB._

    def processMsg(msg: Msg)(implicit ctx: ActorContext): Behavior = msg match {
      case Register(ref) =>
        RegistryB(Cons(ref, register))

      case _ =>
        Behavior.same
    }
  }

  class MainSystem extends ActorSystem("registry-sys") {
    override def run(): Unit = {
      val registry = spawn(RegistryB(), "registry")
      val master = spawn(MainB(registry), "master")

      master ! MainB.Init
    }
  }

  @extern
  def main(args: Array[String]): Unit = {
    val system = new MainSystem
    system.run()
  }

}
