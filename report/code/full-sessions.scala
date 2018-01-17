
trait Nat
trait Z extends Nat
trait S[N <: Nat] extends Nat

trait :!:[A, P]
trait :?:[A, P]
trait :&:[P, Q]
trait :+:[P, Q]
trait Rec[P]
trait Var[N <: Nat]
trait Eps

trait Dual[P] {
  type Out
}

object Dual {
  implicit def sendDual[A, P](implicit pDual: Dual[P]): Dual[A :!: P] =
    new Dual[A :!: P] {
      type Out = A :?: pDual.Out
    }

  implicit def recvDual[A, P](implicit pDual: Dual[P]): Dual[A :?: P] =
    new Dual[A :?: P] {
      type Out = A :!: pDual.Out
    }

  implicit def offerDual[P, Q](implicit pDual: Dual[P], qDual: Dual[Q]): Dual[P :&: Q] =
    new Dual[P :&: Q] {
      type Out = pDual.Out :+: qDual.Out
    }

  implicit def chooseDual[P, Q](implicit pDual: Dual[P], qDual: Dual[Q]): Dual[P :+: Q] =
    new Dual[P :+: Q] {
      type Out = pDual.Out :&: qDual.Out
    }

  implicit def recDual[P](implicit pDual: Dual[P]): Dual[Rec[P]] =
    new Dual[Rec[P]] {
      type Out = Rec[pDual.Out]
    }

  implicit def varDual[N <: Nat]: Dual[Var[N]] =
    new Dual[Var[N]] {
      type Out = Var[N]
    }

  implicit def epsDual: Dual[Eps] =
    new Dual[Eps] {
      type Out = Eps
    }

    def apply[P](implicit ev: Dual[P]): Dual[P] = ev
}

final case class UChan() {
  private[sessions]
  def unsafeWrite[A](x: A): Unit = {
    ???
  }

  private[sessions]
  def unsafeRead[A](): A = {
    ???
  }
}

trait IxFunctor[F[_, _, _]] {
  def map[I, J, A, B](f: A => B)(fa: F[I, J, A]): F[I, J, B]
}

object IxFunctor {
  def apply[F[_, _, _]](implicit ev: IxFunctor[F]): IxFunctor[F] = ev

  implicit class IxFunctorOps[F[_, _, _], I, J, A](val self: F[I, J, A]) extends AnyVal {
    def map[B](f: A => B)(implicit IF: IxFunctor[F]): F[I, J, B] = IF.map(f)(self)
  }
}

trait IxMonad[M[_, _, _]] {
  def pure[I, A](x: A): M[I, I, A]

  def flatMap[I, J, A, K, B](f: A => M[J, K, B])(fija: M[I, J, A]): M[I, K, B]
}

object IxMonad {
  def apply[F[_, _, _]](implicit ev: IxMonad[F]): IxMonad[F] = ev

  implicit class IxMonadOps[M[_, _, _], I, J, A](val self: M[I, J, A]) extends AnyVal {
    def flatMap[K, B](f: A => M[J, K, B])(implicit IM: IxMonad[M]): M[I, K, B] = IM.flatMap(f)(self)
  }
}

final case class Session[S, T, A](run: UChan => A)

object Session {

  private[Session] def const[A, B](x: A): B => A = y => x

  implicit val sessionFunctor: IxFunctor[Session] = new IxFunctor[Session] {
    def map[J, K, A, B](f: A => B)(self: Session[J, K, A]): Session[J, K, B] =
      Session { chan => f(self run chan) }
  }

  implicit val sessionMonad: IxMonad[Session] = new IxMonad[Session] {
    def pure[I, A](x: A): Session[I, I, A] = {
      Session { const(x) }
    }

    def flatMap[I, J, A, K, B](f: A => Session[J, K, B])(self: Session[I, J, A]): Session[I, K, B] = {
      Session { chan =>
        val a = self run chan
        f(a) run chan
      }
    }
  }

  def pure[A, S](x: A): Session[S, S, A] = {
    IxMonad[Session].pure(x)
  }

  def send[A, R](x: A): Session[A :!: R, R, Unit] = {
    Session { _.unsafeWrite(x) }
  }

  def recv[A, R]: Session[A :?: R, R, A] = {
    Session { _.unsafeRead() }
  }

  def close: Session[Eps, Unit, Unit] = {
    Session { const(()) }
  }

  def enter[A, R]: Session[Rec[R], R, A] = {
    ???
  }

}

object test {

  import IxFunctor._
  import IxMonad._
  import Session._

  type Atm         = Id :?: Rec[AtmInner] :+: Eps
  type AtmInner    = AtmDeposit :&: AtmWithdraw :&: Quit
  type Id          = String
  type Quit        = Eps
  type AtmDeposit  = Long :?: Long   :!: Var[Z]
  type AtmWithdraw = Long :?: Var[Z] :+: Var[Z]

  val atmDual = Dual[Atm]
  type Client = atmDual.Out

  val atm: Session[Atm, Unit, Unit] = for {
    id <- recv[Id, Rec[AtmInner] :+: Eps]
    _ <- enter[Unit, AtmInner :+: Eps]
  } yield ()

  def atmInner(id: Id): Session[AtmInner, Unit, Unit] = ???

  // val p: Session[Int :?: Int :!: Eps, Unit, Int] = for {
  //   x <- recv[Int, Int :!: Eps]
  //   y = x * 2
  //   _ <- send[Int, Eps](y)
  //   _ <- close
  // } yield y

}

