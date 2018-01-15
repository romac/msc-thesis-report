
import stainless.lang._
import stainless.annotation._

object sess {

  case class Greet()
  case class Hello()
  case class Bye()
  case class Quit()

  case class :&:[S, T]()
  case class :+:[S, T]()
  case class Send[A, S]()
  case class Recv[A, S]()
  case class Eps()

  case class Alice(session: Session[Send[Greet, Recv[Hello, Alice] :&: Recv[Bye, Eps]] :+: Send[Quit, Eps]])

  case class Session[S](channel: S) {
    def rec: S = channel
  }

  implicit class SessionSend[A, S](val session: Session[Send[A, S]]) {
    @extern
    def send(a: A): Session[S] = ???
  }

  implicit class SessionRecv[A, S](val session: Session[Recv[A, S]]) {
    @extern
    def recv: (A, Session[S]) = ???
  }

  implicit class SessionBranch[S, T](val session: Session[S :&: T]) {
    @extern
    def branch: Either[Session[S], Session[T]] = ???
  }

  implicit class SessionSelect[S, T](val session: Session[S :+: T]) {
    @extern
    def select1: Session[S] = ???
    @extern
    def select2: Session[T] = ???
  }

  implicit class SessionEnd(val session: Session[Eps]) {
    def end: Unit = ()
  }

  @extern
  def quit: Boolean = ???

  def alice(a: Alice): Unit = {
    if (quit) {
      a.session.select2.send(Quit()).end
    } else {
      a.session.select1.send(Greet()).branch match {
        case Left(hello) => alice(hello.recv._2.rec)
        case Right(bye)  => bye.recv._2.end
      }
    }
  }

}
