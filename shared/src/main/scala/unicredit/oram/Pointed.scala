package unicredit.oram


trait Pointed[A] { def empty: A }

class APointed[A](val empty: A) extends Pointed[A]

object Pointed {
  def apply[A](a: A) = new APointed(a)
}