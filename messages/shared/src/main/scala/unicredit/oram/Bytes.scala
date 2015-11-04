package unicredit.oram

import scala.math.BigInt


object Bytes {
  def toInt(xs: Array[Byte]) = BigInt(xs).toInt

  def apply(n: Int): Array[Byte] = {
    val head = BigInt(n).toByteArray
    val padding = Array.fill[Byte](4 - head.length)(0)

    padding ++ head
  }
}