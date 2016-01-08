package unicredit.lethe

import java.util.Random
import scala.collection.mutable.{ BitSet => MBitSet }
import scala.collection.BitSet

import boopickle.Default._


class Path(val int: Int) {
  val bits: BitSet = MBitSet.fromBitMaskNoCopy(Array(int.toLong))
  def apply(i: Int) = bits(i)
  def level = bits.max
  def take(k: Int) = new Path(int >> ((level - k) max 0))

  override def equals(other: Any) = other match {
    case p: Path => p.int == int
    case _ => false
  }

  override def toString = (0 to level).reverse.
    map({ b => if (bits(b)) '1' else '0' }).
    mkString("Path[",  ",", "]")
}

object Path {
  def apply(n: Int) = new Path(n)
  def random(L: Int)(implicit rng: Random) = {
    val cap = pow(2, L)
    new Path(rng.nextInt(cap) + cap)
  }

  implicit val pathPickler = transformPickler[Path, Int](_.int, apply)
}