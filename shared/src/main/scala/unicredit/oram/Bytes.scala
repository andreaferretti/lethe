package unicredit.oram

import scala.math.BigInt


object Bytes {
  def toInt(xs: Array[Byte]) = BigInt(xs).toInt
  def toString(xs: Array[Byte]) = new String(xs,  "UTF-8")
  def toArraySeq(xs: Array[Byte]) = {
    val n = Bytes.toInt(xs take 4)
    var rest = xs drop 4
    var result = Vector.empty[Array[Byte]]
    for (i <- 1 to n) {
      val m = Bytes.toInt(rest take 4)
      rest = rest drop 4
      result :+= rest take m
      rest = rest drop m
    }

    result
  }


  def apply(n: Int): Array[Byte] = {
    val head = BigInt(n).toByteArray
    val padding = Array.fill[Byte](4 - head.length)(0)

    padding ++ head
  }
  def apply(s: String): Array[Byte] = s.getBytes("UTF-8")
  def apply(xs: Seq[Array[Byte]]): Array[Byte] =
    Bytes(xs.length) ++ xs.flatMap(x => (Bytes(x.length) ++ x))
}