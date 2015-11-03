package unicredit.oram.sync

import java.util.Random
import java.nio.ByteBuffer
import scala.collection.mutable.{ BitSet => MBitSet }
import scala.collection.BitSet


trait PathORAM[Id, Doc] extends ORAM[Id, Doc] with Client[Id, Doc] {
  def rng: Random
  def L: Int

  override def readAndRemove(id: Id) = ???

  override def add(id: Id, doc: Doc) = ???

  sealed trait Op
  case object Read extends Op
  case object Write extends Op

  implicit class RichInt(val n: Int) {
    def \(k: Int) = if (n % k == 0) n / k else n / k + 1
  }

  class Path(bits: BitSet, n: Int) {
    def apply(i: Int) = if (i < n) bits(i) else false
    def length = n

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

  def access(op: Op, id: Id, doc: Doc) = {

  }
}

class MyPathORAM(val remote: Remote, val passPhrase: String)
  extends PathORAM[Int, String] with AESClient[Int, String] {
  import boopickle.Default._

  implicit val pickle = generatePickler[(Int, String)]
  def empty = ""
  val L = 16
}