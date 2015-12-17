package unicredit.oram
package sync

import java.util.Random

import transport.Remote
import client._
import storage._


object MultiORAM {
  import boopickle.Default._
  import java.security.SecureRandom

  def gen2[
    Id1: Pointed: Pickler,
    Doc1: Pointed: Pickler,
    Id2: Pointed: Pickler,
    Doc2: Pointed: Pickler
  ](remote: Remote, passPhrase: String, params: Params) = {
    import Wrap2._

    type K = Wrap2[Id1, Id2]
    type V = Wrap2[Doc1, Doc2]
    type K1 = El1[Id1, Id2]
    type V1 = El1[Doc1, Doc2]
    type K2 = El2[Id1, Id2]
    type V2 = El2[Doc1, Doc2]
    implicit val p1 = implicitly[Pointed[Id1]].map(El1[Id1, Id2](_))
    implicit val p2 = implicitly[Pointed[Id2]].map(El2[Id1, Id2](_))
    implicit val p3 = implicitly[Pointed[Doc1]].map(El1[Doc1, Doc2](_))
    implicit val p4 = implicitly[Pointed[Doc2]].map(El2[Doc1, Doc2](_))
    val (o1, o2) = make2[K, V, K1, V1, K2, V2](remote, passPhrase, params)

    (
      new WrapORAM[K, V, K1, V1, Id1, Doc1](o1, el1[Id1, Id2], el1[Doc1, Doc2]),
      new WrapORAM[K, V, K2, V2, Id2, Doc2](o2, el2[Id1, Id2], el2[Doc1, Doc2])
    )
  }

  def gen3[
    Id1: Pointed: Pickler,
    Doc1: Pointed: Pickler,
    Id2: Pointed: Pickler,
    Doc2: Pointed: Pickler,
    Id3: Pointed: Pickler,
    Doc3: Pointed: Pickler
  ](remote: Remote, passPhrase: String, params: Params) = {
    import Wrap3._

    type K = Wrap3[Id1, Id2, Id3]
    type V = Wrap3[Doc1, Doc2, Doc3]
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

  def gen4[
    Id1: Pointed: Pickler,
    Doc1: Pointed: Pickler,
    Id2: Pointed: Pickler,
    Doc2: Pointed: Pickler,
    Id3: Pointed: Pickler,
    Doc3: Pointed: Pickler,
    Id4: Pointed: Pickler,
    Doc4: Pointed: Pickler
  ](remote: Remote, passPhrase: String, params: Params) = {
    import Wrap4._

    type K = Wrap4[Id1, Id2, Id3, Id4]
    type V = Wrap4[Doc1, Doc2, Doc3, Doc4]
    type K1 = El1[Id1, Id2, Id3, Id4]
    type V1 = El1[Doc1, Doc2, Doc3, Doc4]
    type K2 = El2[Id1, Id2, Id3, Id4]
    type V2 = El2[Doc1, Doc2, Doc3, Doc4]
    type K3 = El3[Id1, Id2, Id3, Id4]
    type V3 = El3[Doc1, Doc2, Doc3, Doc4]
    type K4 = El4[Id1, Id2, Id3, Id4]
    type V4 = El4[Doc1, Doc2, Doc3, Doc4]
    implicit val p1 = implicitly[Pointed[Id1]].map(El1[Id1, Id2, Id3, Id4](_))
    implicit val p2 = implicitly[Pointed[Id2]].map(El2[Id1, Id2, Id3, Id4](_))
    implicit val p3 = implicitly[Pointed[Id3]].map(El3[Id1, Id2, Id3, Id4](_))
    implicit val p4 = implicitly[Pointed[Id4]].map(El4[Id1, Id2, Id3, Id4](_))
    implicit val p5 = implicitly[Pointed[Doc1]].map(El1[Doc1, Doc2, Doc3, Doc4](_))
    implicit val p6 = implicitly[Pointed[Doc2]].map(El2[Doc1, Doc2, Doc3, Doc4](_))
    implicit val p7 = implicitly[Pointed[Doc3]].map(El3[Doc1, Doc2, Doc3, Doc4](_))
    implicit val p8 = implicitly[Pointed[Doc4]].map(El4[Doc1, Doc2, Doc3, Doc4](_))
    val (o1, o2, o3, o4) =
      make4[K, V, K1, V1, K2, V2, K3, V3, K4, V4](remote, passPhrase, params)

    (
      new WrapORAM[K, V, K1, V1, Id1, Doc1](o1, el1[Id1, Id2, Id3, Id4], el1[Doc1, Doc2, Doc3, Doc4]),
      new WrapORAM[K, V, K2, V2, Id2, Doc2](o2, el2[Id1, Id2, Id3, Id4], el2[Doc1, Doc2, Doc3, Doc4]),
      new WrapORAM[K, V, K3, V3, Id3, Doc3](o3, el3[Id1, Id2, Id3, Id4], el3[Doc1, Doc2, Doc3, Doc4]),
      new WrapORAM[K, V, K4, V4, Id4, Doc4](o4, el4[Id1, Id2, Id3, Id4], el4[Doc1, Doc2, Doc3, Doc4])
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