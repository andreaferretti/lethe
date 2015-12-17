package unicredit.oram
package sync

import java.util.Random

import transport.Remote
import client._
import storage._


object MultiORAM {
  import boopickle.Default._
  import java.security.SecureRandom

  def leftBijection[A, B] = Bijection[A, Left[A, B]](Left(_), _.left.get)
  def rightBijection[A, B] = Bijection[B, Right[A, B]](Right(_), _.right.get)

  sealed trait Either3[A, B, C] {
    def el1: A = ???
    def el2: B = ???
    def el3: C = ???
  }
  case class El1[A, B, C](x: A) extends Either3[A, B, C] {
    override def el1 = x
  }
  case class El2[A, B, C](x: B) extends Either3[A, B, C] {
    override def el2 = x
  }
  case class El3[A, B, C](x: C) extends Either3[A, B, C] {
    override def el3 = x
  }

  def el1[A, B, C] = Bijection[A, El1[A, B, C]](El1(_), _.el1)
  def el2[A, B, C] = Bijection[B, El2[A, B, C]](El2(_), _.el2)
  def el3[A, B, C] = Bijection[C, El3[A, B, C]](El3(_), _.el3)

  def pair[
    Id1: Pointed: Pickler,
    Doc1: Pointed: Pickler,
    Id2: Pointed: Pickler,
    Doc2: Pointed: Pickler
  ](remote: Remote, passPhrase: String, params: Params) = {
    type K = Either[Id1, Id2]
    type V = Either[Doc1, Doc2]
    type K1 = Left[Id1, Id2]
    type V1 = Left[Doc1, Doc2]
    type K2 = Right[Id1, Id2]
    type V2 = Right[Doc1, Doc2]
    implicit val p1 = implicitly[Pointed[Id1]].map(Left[Id1, Id2](_))
    implicit val p2 = implicitly[Pointed[Id2]].map(Right[Id1, Id2](_))
    implicit val p3 = implicitly[Pointed[Doc1]].map(Left[Doc1, Doc2](_))
    implicit val p4 = implicitly[Pointed[Doc2]].map(Right[Doc1, Doc2](_))
    val (left, right) =
      make2[K, V, K1, V1, K2, V2](remote, passPhrase, params)

    (
      new WrapORAM[K, V, K1, V1, Id1, Doc1](left, leftBijection[Id1, Id2], leftBijection[Doc1, Doc2]),
      new WrapORAM[K, V, K2, V2, Id2, Doc2](right, rightBijection[Id1, Id2], rightBijection[Doc1, Doc2])
    )
  }

  def triple[
    Id1: Pointed: Pickler,
    Doc1: Pointed: Pickler,
    Id2: Pointed: Pickler,
    Doc2: Pointed: Pickler,
    Id3: Pointed: Pickler,
    Doc3: Pointed: Pickler
  ](remote: Remote, passPhrase: String, params: Params) = {
    type K = Either3[Id1, Id2, Id3]
    type V = Either3[Doc1, Doc2, Doc3]
    type K1 = El1[Id1, Id2, Id3]
    type V1 = El1[Doc1, Doc2, Doc3]
    type K2 = El2[Id1, Id2, Id3]
    type V2 = El2[Doc1, Doc2, Doc3]
    type K3 = El3[Id1, Id2, Id3]
    type V3 = El3[Doc1, Doc2, Doc3]
    implicit val p1 = implicitly[Pointed[Id1]].map(El1[Id1, Id2, Id3](_))
    implicit val p2 = implicitly[Pointed[Id2]].map(El2[Id1, Id2, Id3](_))
    implicit val p3 = implicitly[Pointed[Id3]].map(El3[Id1, Id2, Id3](_))
    implicit val p4 = implicitly[Pointed[Doc1]].map(El1[Doc1, Doc2, Doc3](_))
    implicit val p5 = implicitly[Pointed[Doc2]].map(El2[Doc1, Doc2, Doc3](_))
    implicit val p6 = implicitly[Pointed[Doc3]].map(El3[Doc1, Doc2, Doc3](_))
    val (o1, o2, o3) =
      make3[K, V, K1, V1, K2, V2, K3, V3](remote, passPhrase, params)

    (
      new WrapORAM[K, V, K1, V1, Id1, Doc1](o1, el1[Id1, Id2, Id3], el1[Doc1, Doc2, Doc3]),
      new WrapORAM[K, V, K2, V2, Id2, Doc2](o2, el2[Id1, Id2, Id3], el2[Doc1, Doc2, Doc3]),
      new WrapORAM[K, V, K3, V3, Id3, Doc3](o3, el3[Id1, Id2, Id3], el3[Doc1, Doc2, Doc3])
    )
  }

  def make2[
    K: Pickler,
    V: Pickler,
    Id1 <: K : Pointed,
    Doc1 <: V : Pointed,
    Id2 <: K: Pointed,
    Doc2 <: V : Pointed
  ](remote: Remote, passPhrase: String, params: Params) = {
    implicit val rng = new SecureRandom
    val client = StandardClient[(K, V)](remote, passPhrase)
    val stash = MapStash.empty[K, V]
    val index = MapIndex[K](params.depth)

    (
      new PathORAM[K, V, Id1, Doc1](client, stash, index, params),
      new PathORAM[K, V, Id2, Doc2](client, stash, index, params)
    )
  }

  def make3[
    K: Pickler,
    V: Pickler,
    Id1 <: K : Pointed,
    Doc1 <: V : Pointed,
    Id2 <: K: Pointed,
    Doc2 <: V : Pointed,
    Id3 <: K: Pointed,
    Doc3 <: V : Pointed
  ](remote: Remote, passPhrase: String, params: Params) = {
    implicit val rng = new SecureRandom
    val client = StandardClient[(K, V)](remote, passPhrase)
    val stash = MapStash.empty[K, V]
    val index = MapIndex[K](params.depth)

    (
      new PathORAM[K, V, Id1, Doc1](client, stash, index, params),
      new PathORAM[K, V, Id2, Doc2](client, stash, index, params),
      new PathORAM[K, V, Id3, Doc3](client, stash, index, params)
    )
  }

  def make4[
    K: Pickler,
    V: Pickler,
    Id1 <: K : Pointed,
    Doc1 <: V : Pointed,
    Id2 <: K: Pointed,
    Doc2 <: V : Pointed,
    Id3 <: K: Pointed,
    Doc3 <: V : Pointed,
    Id4 <: K: Pointed,
    Doc4 <: V : Pointed
  ](remote: Remote, passPhrase: String, params: Params) = {
    implicit val rng = new SecureRandom
    val client = StandardClient[(K, V)](remote, passPhrase)
    val stash = MapStash.empty[K, V]
    val index = MapIndex[K](params.depth)

    (
      new PathORAM[K, V, Id1, Doc1](client, stash, index, params),
      new PathORAM[K, V, Id2, Doc2](client, stash, index, params),
      new PathORAM[K, V, Id3, Doc3](client, stash, index, params),
      new PathORAM[K, V, Id4, Doc4](client, stash, index, params)
    )
  }
}