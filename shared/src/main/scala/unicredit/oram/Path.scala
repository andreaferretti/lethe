package unicredit.oram

import java.util.Random
import scala.collection.mutable.{ BitSet => MBitSet }
import scala.collection.BitSet


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
  def pow(n: Int, k:Int): Int = k match {
    case kk if kk < 0  => 0  // error
    case 0 => 1
    case 1 => n
    case 2 => n*n
    case kk  if kk % 2 == 0  => pow(pow(n, k/2), 2)
    case _  => pow(n, k/2)*pow(n, (k+1)/2)
  }

  def apply(n: Int) = new Path(n)
  def random(L: Int)(implicit rng: Random) = {
    val cap = pow(2, L)
    new Path(rng.nextInt(cap) + cap)
  }
}