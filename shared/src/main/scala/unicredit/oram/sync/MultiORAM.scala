package unicredit.oram
package sync

import java.util.Random

import transport.Remote
import client._
import storage._


case class Bijection[A, B](from: A => B, to: B => A)

class WrapORAM[K, V, K1 <: K, V1 <:V, Id, Doc] (
  inner: PathORAM[K, V, K1, V1],
  bijId: Bijection[Id, K1],
  bijDoc: Bijection[Doc, V1]
) extends ORAM[Id, Doc] {
  override def read(id: Id) = bijDoc.to(inner.read(bijId.from(id)))

  override def write(id: Id, doc: Doc) =
    inner.write(bijId.from(id), bijDoc.from(doc))

  override def init = inner.init
}

object MultiORAM {
  import boopickle.Default._
  import java.security.SecureRandom

  def leftBijection[A, B] = Bijection[A, Left[A, B]](Left(_), _.left.get)
  def rightBijection[A, B] = Bijection[B, Right[A, B]](Right(_), _.right.get)

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