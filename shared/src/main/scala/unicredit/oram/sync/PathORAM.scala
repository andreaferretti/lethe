package unicredit.oram
package sync

import java.util.Random


trait PathORAMProtocol[Id, Doc] extends ORAMProtocol[Id, Doc] { self: Client[Id, Doc] =>
  type Bucket = Seq[(Id, Doc)]
  implicit def rng: Random
  def L: Int
  def Z: Int
  var position = Map.empty[Id, Path]
  var stash = Map.empty[Id, Doc]

  override def readAndRemove(id: Id) = ???

  override def add(id: Id, doc: Doc) = ???

  sealed trait Op
  case object Read extends Op
  case object Write extends Op

  def readBucket(p: Path, ℓ: Int): Bucket = {
    val start = p.take(ℓ).int * Z
    (0 until Z) map { i => fetchClear(start + i) }
  }

  def writeBucket(p: Path, ℓ: Int, stash: Bucket): Unit = {
    var position = p.take(ℓ).int * Z
    for (data <- stash) {
      putClear(position, data)
      position += 1
    }
  }

  def access(op: Op, id: Id, doc: Doc) = {
    val x = position.getOrElse(id, Path.random(L))
    position += (id -> Path.random(L))
    for (ℓ <- 0 to L) {
      stash ++= readBucket(x, ℓ)
    }
    val data = stash.getOrElse(id, empty)
    if (op == Write) {
      stash += (id -> doc)
    }
    for (ℓ <- (0 to L).reverse) {
      var stash_ = stash filter { case (a, _) => x.take(ℓ) == position(a).take(ℓ) }
      stash_ = stash_ take Z
      stash --= stash_.keySet
      writeBucket(x, ℓ, stash_.toSeq)
    }
    data
  }
}

class PathORAM(val remote: Remote, val passPhrase: String)
  extends PathORAMProtocol[Int, String] with AESClient[Int, String] {
  import boopickle.Default._

  implicit val pickle = generatePickler[(Int, String)]
  def empty = ""
  val L = 16
  val Z = 4
}