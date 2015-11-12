package unicredit.oram
package sync

import client._
import storage._
import transport.Remote


class PathORAM[K, V, Id <: K : Pointed, Doc <: V : Pointed](
  client: StandardClient[(K, V)],
  stash: Stash[K, V],
  index: Index[K],
  L: Int,
  Z: Int,
  offset: Int
) extends ORAM[Id, Doc] {
  sealed trait Op
  case object Read extends Op
  case object Write extends Op

  type Bucket = Seq[(K, V)]
  val emptyId = implicitly[Pointed[Id]].empty
  val emptyDoc = implicitly[Pointed[Doc]].empty

  override def read(id: Id) = access(Read, id, emptyDoc)
  override def write(id: Id, doc: Doc) = access(Write, id, doc)

  def fetchBucket(p: Path, ℓ: Int): Bucket = {
    val start = (p.take(ℓ).int - 1 + offset) * Z
    (0 until Z) map { i => client.fetchClear(start + i) }
  }

  def putBucket(p: Path, ℓ: Int, bucket: Bucket): Unit = {
    val start = (p.take(ℓ).int - 1 + offset) * Z
    for (i <- 0 until Z) {
      val item = if (i < bucket.length) bucket(i) else (emptyId, emptyDoc)
      client.putClear(start + i, item)
    }
  }

  def access(op: Op, id: Id, doc: Doc) = {
    val x = index.getPosition(id)
    index.putRandom(id)
    for (ℓ <- 0 to L) {
      stash ++= fetchBucket(x, ℓ)
    }
    val data = stash.getOrElse(id, emptyDoc)
    if (op == Write) {
      stash ++= List(id -> doc)
    }
    stash filter { case (a, _) => a != emptyId }
    for (ℓ <- (0 to L).reverse) {
      val stash_ = stash.take(Z){ case (a, _) =>
        x.take(ℓ) == index.getPosition(a).take(ℓ)
      }
      stash --= stash_.keySet
      putBucket(x, ℓ, stash_.toSeq)
    }
    data.asInstanceOf[Doc]
  }

  def init: Unit = {
    val numBuckets = (pow(2, L + 1) - 1) * Z
    val items = (0 until numBuckets) map { _ => (emptyId, emptyDoc) }

    client.init(items, offset * Z)
    index.init
  }
}

object PathORAM {
  import boopickle.Default._
  import java.security.SecureRandom

  def apply[K: Pickler, V: Pickler, Id <: K : Pointed, Doc <: V : Pointed](
    remote: Remote,
    passPhrase: String,
    L: Int,
    Z: Int,
    offset: Int = 0
  ) = {
    val rng = new SecureRandom

    new PathORAM(StandardClient[(K, V)](remote, passPhrase),
      MapStash.empty[K, V], MapIndex[K](L)(rng), L, Z, offset)
  }

  def recursive[Id: Pointed: Pickler, Doc: Pointed: Pickler, Bin: Pointed: Pickler](
    remote: Remote,
    passPhrase: String,
    L: Int,
    Z: Int,
    offset: Int,
    bin: Id => Bin
  ) = {
    val rng = new SecureRandom
    val index = ORAMIndex.local[Id, Bin](
      remote, passPhrase, L, Z, offset + pow(2, L + 1) - 1, bin)
    val client = StandardClient[(Id, Doc)](remote, passPhrase)
    val stash = MapStash.empty[Id, Doc]

    new PathORAM[Id, Doc, Id, Doc](client, stash, index, L, Z, offset)
  }
}