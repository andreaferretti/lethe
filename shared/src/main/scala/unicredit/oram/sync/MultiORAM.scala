package unicredit.oram
package sync

import java.util.Random

import transport.Remote
import client._
import storage._


class LeftMultiORAM[Id, Doc, Id1, Doc1] (
  inner: PathORAM[Either[Id, Id1], Either[Doc, Doc1], Left[Id, Id1], Left[Doc, Doc1]]
) extends ORAM[Id, Doc] {
  override def read(id: Id) = inner.read(Left(id)).left.get

  override def write(id: Id, doc: Doc) = inner.write(Left(id), Left(doc))

  override def init = inner.init
}

class RightMultiORAM[Id, Doc, Id1, Doc1] (
  inner: PathORAM[Either[Id, Id1], Either[Doc, Doc1], Right[Id, Id1], Right[Doc, Doc1]]
) extends ORAM[Id1, Doc1] {
  override def read(id: Id1) = inner.read(Right(id)).right.get

  override def write(id: Id1, doc: Doc1) = inner.write(Right(id), Right(doc))

  override def init = ()
}


object MultiORAM {
  import boopickle.Default._
  import java.security.SecureRandom

  def pair[
    Id: Pointed: Pickler,
    Doc: Pointed: Pickler,
    Id1: Pointed: Pickler,
    Doc1: Pointed: Pickler
  ](remote: Remote, passPhrase: String, params: Params) = {
    type K = Either[Id, Id1]
    type V = Either[Doc, Doc1]
    type K1 = Left[Id, Id1]
    type V1 = Left[Doc, Doc1]
    type K2 = Right[Id, Id1]
    type V2 = Right[Doc, Doc1]
    implicit val p1 = implicitly[Pointed[Id]].map(Left[Id, Id1](_))
    implicit val p2 = implicitly[Pointed[Id1]].map(Right[Id, Id1](_))
    implicit val p3 = implicitly[Pointed[Doc]].map(Left[Doc, Doc1](_))
    implicit val p4 = implicitly[Pointed[Doc1]].map(Right[Doc, Doc1](_))
    val (left, right) =
      make2[K, V, K1, V1, K2, V2](remote, passPhrase, params)

    (new LeftMultiORAM(left), new RightMultiORAM(right))
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
}