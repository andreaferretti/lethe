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

  def left[Id: Pointed, Doc: Pointed, Id1, Doc1](
    client: StandardClient[(Either[Id, Id1], Either[Doc, Doc1])],
    stash: Stash[Either[Id, Id1], Either[Doc, Doc1]],
    index: Index[Either[Id, Id1]],
    params: Params
  ): ORAM[Id, Doc] = {
      implicit val p1 = Pointed(Left[Id, Id1](implicitly[Pointed[Id]].empty))
      implicit val p2 = Pointed(Left[Doc, Doc1](implicitly[Pointed[Doc]].empty))
      val inner = new PathORAM[
        Either[Id, Id1],
        Either[Doc, Doc1],
        Left[Id, Id1],
        Left[Doc, Doc1]](client, stash, index, params)

      new LeftMultiORAM(inner)
    }

  def right[Id, Doc, Id1: Pointed, Doc1: Pointed](
    client: StandardClient[(Either[Id, Id1], Either[Doc, Doc1])],
    stash: Stash[Either[Id, Id1], Either[Doc, Doc1]],
    index: Index[Either[Id, Id1]],
    params: Params
  ): ORAM[Id1, Doc1] = {
      implicit val p1 = Pointed(Right[Id, Id1](implicitly[Pointed[Id1]].empty))
      implicit val p2 = Pointed(Right[Doc, Doc1](implicitly[Pointed[Doc1]].empty))
      val inner = new PathORAM[
        Either[Id, Id1],
        Either[Doc, Doc1],
        Right[Id, Id1],
        Right[Doc, Doc1]](client, stash, index, params)

      new RightMultiORAM(inner)
    }

  def pair[
    Id: Pointed: Pickler,
    Doc: Pointed: Pickler,
    Id1: Pointed: Pickler,
    Doc1: Pointed: Pickler
  ](remote: Remote, passPhrase: String, params: Params) = {
    implicit val rng = new SecureRandom
    val client = StandardClient[(Either[Id, Id1], Either[Doc, Doc1])](remote, passPhrase)
    val stash = MapStash.empty[Either[Id, Id1], Either[Doc, Doc1]]
    val index = MapIndex[Either[Id, Id1]](params.depth)

    (
      left[Id, Doc, Id1, Doc1](client, stash, index, params),
      right[Id, Doc, Id1, Doc1](client, stash, index, params)
    )
  }
}