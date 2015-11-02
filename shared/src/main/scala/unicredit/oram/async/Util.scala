package unicredit.oram.async

import scala.concurrent.{ Future, ExecutionContext }


object Util {
  def foldFutures[A, B, C](xs: Seq[A], f: A => Future[B], empty: C,
    combine: (C, B) => C)(implicit ec: ExecutionContext): Future[C] =
      xs.foldLeft(Future(empty)) { (future, x) =>
        for {
          c <- future
          b <- f(x)
        } yield combine(c, b)
      }

  def sequentially[A](xs: Seq[A])(f: A => Future[Unit])
    (implicit ec: ExecutionContext) =
      foldFutures(xs, f, (), { (a: Unit, b: Unit) => () })
}