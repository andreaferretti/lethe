package unicredit.oram
package sync

import java.util.Random

import client._
import storage._


trait PathORAM[K, V, Id <: K, Doc <: V] extends ORAM[Id, Doc] {
  type Bucket = Seq[(K, V)]
  def client: StandardClient[(K, V)]
  implicit def rng: Random
  def L: Int
  def Z: Int
  def offset: Int
  def emptyID: Id
  def stash: Stash[K, V]

  def getPosition(id: K): Path
  def putPosition(id: K, path: Path): Unit

  override def read(id: Id) = access(Read, id, empty)

  override def write(id: Id, doc: Doc) = access(Write, id, doc)

  sealed trait Op
  case object Read extends Op
  case object Write extends Op

  def fetchBucket(p: Path, ℓ: Int): Bucket = {
    val start = (p.take(ℓ).int - 1) * Z + offset
    (0 until Z) map { i => client.fetchClear(start + i) }
  }

  def putBucket(p: Path, ℓ: Int, bucket: Bucket): Unit = {
    val start = (p.take(ℓ).int - 1) * Z + offset
    for (i <- 0 until Z) {
      val item = if (i < bucket.length) bucket(i) else (emptyID, empty)
      client.putClear(start + i, item)
    }
  }

  def access(op: Op, id: Id, doc: Doc) = {
    val x = getPosition(id)
    putPosition(id, Path.random(L))
    for (ℓ <- 0 to L) {
      stash ++= fetchBucket(x, ℓ)
    }
    val data = stash.getOrElse(id, empty)
    if (op == Write) {
      stash ++= List(id -> doc)
    }
    stash filter { case (a, _) => a != emptyID }
    for (ℓ <- (0 to L).reverse) {
      val stash_ = stash.take(Z){ case (a, _) =>
        x.take(ℓ) == getPosition(a).take(ℓ)
      }
      stash --= stash_.keySet
      putBucket(x, ℓ, stash_.toSeq)
    }
    data.asInstanceOf[Doc]
  }

  def init: Unit = {
    val numBuckets = (math.pow(2, L + 1).toInt - 1) * Z
    val items = (0 until numBuckets) map { _ => (emptyID, empty) }
    client.init(items)
  }
}