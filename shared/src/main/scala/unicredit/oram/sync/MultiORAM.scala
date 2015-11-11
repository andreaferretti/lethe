package unicredit.oram
package sync

import java.util.Random

import transport.Remote
import client._


class LeftMultiORAM[Id, Doc, Id1, Doc1] (
  inner: LocalPathORAM[Either[Id, Id1], Either[Doc, Doc1], Left[Id, Id1], Left[Doc, Doc1]]
) extends ORAM[Id, Doc] {
  override def empty = inner.empty.left.get

  override def read(id: Id) = inner.read(Left(id)).left.get

  override def write(id: Id, doc: Doc) = inner.write(Left(id), Left(doc))

  override def init = inner.init
}

class RightMultiORAM[Id, Doc, Id1, Doc1] (
  inner: LocalPathORAM[Either[Id, Id1], Either[Doc, Doc1], Right[Id, Id1], Right[Doc, Doc1]]
) extends ORAM[Id1, Doc1] {
  override def empty = inner.empty.right.get

  override def read(id: Id1) = inner.read(Right(id)).right.get

  override def write(id: Id1, doc: Doc1) = inner.write(Right(id), Right(doc))

  override def init = ()
}

object MultiORAM {
  import boopickle.Default._
  import java.security.SecureRandom


  def left[Id, Doc, Id1, Doc1](
    client: StandardClient[(Either[Id, Id1], Either[Doc, Doc1])],
    rng: Random,
    emptyID: Id,
    empty: Doc,
    L: Int,
    Z: Int
  ): ORAM[Id, Doc] = {
      val inner = new LocalPathORAM[
        Either[Id, Id1],
        Either[Doc, Doc1],
        Left[Id, Id1],
        Left[Doc, Doc1]](client, rng, Left(emptyID), Left(empty), L, Z)

      new LeftMultiORAM(inner)
    }

  def right[Id, Doc, Id1, Doc1](
    client: StandardClient[(Either[Id, Id1], Either[Doc, Doc1])],
    rng: Random,
    emptyID: Id1,
    empty: Doc1,
    L: Int,
    Z: Int
  ): ORAM[Id1, Doc1] = {
      val inner = new LocalPathORAM[
        Either[Id, Id1],
        Either[Doc, Doc1],
        Right[Id, Id1],
        Right[Doc, Doc1]](client, rng, Right(emptyID), Right(empty), L, Z)

      new RightMultiORAM(inner)
    }

  def pair[Id, Doc, Id1, Doc1](
    remote: Remote,
    passPhrase: String,
    emptyID: Id,
    empty: Doc,
    emptyID1: Id1,
    empty1: Doc1,
    L: Int,
    Z: Int
  )(implicit p1: Pickler[Id],
    p2: Pickler[Doc],
    p3: Pickler[Id1],
    p4: Pickler[Doc1]
  ) = {
    val client = StandardClient[(Either[Id, Id1], Either[Doc, Doc1])](remote, passPhrase)
    val rng = new SecureRandom

    (
      left[Id, Doc, Id1, Doc1](client, rng, emptyID, empty, L, Z),
      right[Id, Doc, Id1, Doc1](client, rng, emptyID1, empty1, L, Z)
    )
  }
}