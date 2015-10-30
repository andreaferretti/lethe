package unicredit.oram

import scala.concurrent.{ Future, ExecutionContext }
import scala.math.BigInt


trait UnencryptedClient extends Client[Int, String] {

  def decrypt(a: Array[Byte]) = {
    val n = BigInt(a take 4).toInt

    println("Decrypting ", (a take 4).toList, " which amounts to ", n)

    (n, new String(a drop 4, "UTF-8"))
  }

  def encrypt(data: (Int, String)) = {
    println("About to encrypt ", data)
    val (n, doc) = data
    val head = BigInt(n).toByteArray
    val padding = Array.fill[Byte](4 - head.length)(0)

    println("key is ", (padding ++ head).toList, " which amounts to ", n)

    padding ++ head ++ doc.getBytes("UTF-8")
  }
}