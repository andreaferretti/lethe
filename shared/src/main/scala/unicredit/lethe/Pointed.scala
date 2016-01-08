package unicredit.lethe


trait Pointed[A] {
  def empty: A

  def map[B](f: A => B): Pointed[B] = new APointed(f(empty))
}

class APointed[A](val empty: A) extends Pointed[A]

object Pointed {
  def apply[A](a: A) = new APointed(a)
}