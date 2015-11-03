package unicredit.oram.sync

import java.util.Random
import java.nio.ByteBuffer
import scala.collection.mutable.{ BitSet => MBitSet }
import scala.collection.BitSet


trait PathORAMProtocol[Id, Doc] extends ORAMProtocol[Id, Doc] { self: Client[Id, Doc] =>
  type Bucket = Seq[(Id, Doc)]
  def rng: Random
  def L: Int
  def Z: Int
  var position = Map.empty[Id, Path]
  var stash = Map.empty[Id, Doc]

  override def readAndRemove(id: Id) = ???

  override def add(id: Id, doc: Doc) = ???

  sealed trait Op
  case object Read extends Op
  case object Write extends Op

  implicit class RichInt(val n: Int) {
    def \(k: Int) = if (n % k == 0) n / k else n / k + 1
  }

  class Path(private val bits: BitSet, private val n: Int) {
    def apply(i: Int) = if (i < n) bits(i) else false
    def length = n
    def take(k: Int) = new Path(bits, n min k)

    override def equals(other: Any) = other match {
      case p: Path =>
        val bits1 = p.bits
        val n1 = p.n
        (n == n1) && (0 to n).forall(i => bits(i) == bits1(i))
      case _ => false
    }

    override def toString =
      "Path[" +
      ((0 until n) map { b => if (bits(b)) '1' else '0' } mkString ",") +
      "]"
  }

  def longArray(a: Array[Byte]): Array[Long] = {
    val buf = ByteBuffer.wrap(a).asLongBuffer
    val n = buf.capacity
    val res = Array.ofDim[Long](n)
    for (i <- 0 until n) { res(i) = buf.get }
    res
  }

  def bitsetOfArray(a: Array[Byte]): BitSet = {
    MBitSet.fromBitMaskNoCopy(longArray(a))
  }

  def randomPath(bits: Int) = {
    val bytes = Array.ofDim[Byte](bits \ 64 * 8)
    rng.nextBytes(bytes)
    new Path(bitsetOfArray(bytes), bits)
  }

  def readBucket(p: Path, ℓ: Int): Bucket = {
    ???
  }

  def writeBucket(p: Path, ℓ: Int, stash: Bucket): Unit = {
    ???
  }

  def findInStash(id: Id): Doc = stash.getOrElse(id, empty)

  def access(op: Op, id: Id, doc: Doc) = {
    val x = position.getOrElse(id, randomPath(L))
    position += (id -> randomPath(L))
    for (ℓ <- 0 to L) {
      stash ++= readBucket(x, ℓ)
    }
    val data = findInStash(id)
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