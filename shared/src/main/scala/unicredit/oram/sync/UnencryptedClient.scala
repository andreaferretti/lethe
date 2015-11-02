package unicredit.oram.sync

import scala.math.BigInt


trait UnencryptedClient extends Client[Int, String] {

  def decrypt(a: Array[Byte]) = {
    val n = BigInt(a take 4).toInt

    (n, new String(a drop 4, "UTF-8"))
  }

  def encrypt(data: (Int, String)) = {
    val (n, doc) = data
    val head = BigInt(n).toByteArray
    val padding = Array.fill[Byte](4 - head.length)(0)

    padding ++ head ++ doc.getBytes("UTF-8")
  }
}