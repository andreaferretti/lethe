package unicredit.lethe

import boopickle.Default._


case class Bijection[A, B](from: A => B, to: B => A)

object Wrap2 {
  sealed trait Wrap2[A, B] {
    def el1: A = ???
    def el2: B = ???
  }
  case class El1[A, B](x: A) extends Wrap2[A, B] {
    override def el1 = x
  }
  case class El2[A, B](x: B) extends Wrap2[A, B] {
    override def el2 = x
  }

  def el1[A, B] = Bijection[A, El1[A, B]](El1(_), _.el1)
  def el2[A, B] = Bijection[B, El2[A, B]](El2(_), _.el2)

  implicit def wrap2Pickler[A: Pickler, B: Pickler] =
    compositePickler[Wrap2[A, B]].
      addConcreteType[El1[A, B]].
      addConcreteType[El2[A, B]]
}

object Wrap3 {
  sealed trait Wrap3[A, B, C] {
    def el1: A = ???
    def el2: B = ???
    def el3: C = ???
  }
  case class El1[A, B, C](x: A) extends Wrap3[A, B, C] {
    override def el1 = x
  }
  case class El2[A, B, C](x: B) extends Wrap3[A, B, C] {
    override def el2 = x
  }
  case class El3[A, B, C](x: C) extends Wrap3[A, B, C] {
    override def el3 = x
  }

  def el1[A, B, C] = Bijection[A, El1[A, B, C]](El1(_), _.el1)
  def el2[A, B, C] = Bijection[B, El2[A, B, C]](El2(_), _.el2)
  def el3[A, B, C] = Bijection[C, El3[A, B, C]](El3(_), _.el3)

  implicit def wrap3Pickler[A: Pickler, B: Pickler, C: Pickler] =
    compositePickler[Wrap3[A, B, C]].
      addConcreteType[El1[A, B, C]].
      addConcreteType[El2[A, B, C]].
      addConcreteType[El3[A, B, C]]
}

object Wrap4 {
  sealed trait Wrap4[A, B, C, D] {
    def el1: A = ???
    def el2: B = ???
    def el3: C = ???
    def el4: D = ???
  }
  case class El1[A, B, C, D](x: A) extends Wrap4[A, B, C, D] {
    override def el1 = x
  }
  case class El2[A, B, C, D](x: B) extends Wrap4[A, B, C, D] {
    override def el2 = x
  }
  case class El3[A, B, C, D](x: C) extends Wrap4[A, B, C, D] {
    override def el3 = x
  }
  case class El4[A, B, C, D](x: D) extends Wrap4[A, B, C, D] {
    override def el4 = x
  }

  def el1[A, B, C, D] = Bijection[A, El1[A, B, C, D]](El1(_), _.el1)
  def el2[A, B, C, D] = Bijection[B, El2[A, B, C, D]](El2(_), _.el2)
  def el3[A, B, C, D] = Bijection[C, El3[A, B, C, D]](El3(_), _.el3)
  def el4[A, B, C, D] = Bijection[D, El4[A, B, C, D]](El4(_), _.el4)

  implicit def wrap3Pickler[A: Pickler, B: Pickler, C: Pickler, D: Pickler] =
    compositePickler[Wrap4[A, B, C, D]].
      addConcreteType[El1[A, B, C, D]].
      addConcreteType[El2[A, B, C, D]].
      addConcreteType[El3[A, B, C, D]].
      addConcreteType[El4[A, B, C, D]]
}