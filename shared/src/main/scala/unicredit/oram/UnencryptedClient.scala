package unicredit.oram

import scala.concurrent.{ Future, ExecutionContext }
import scala.math.BigInt


trait UnencryptedClient extends Client[Int, Array[Byte]] {

  def decrypt(a: Array[Byte]) = {
    val n = BigInt(a take 4).toInt

    println("Decrypting ", (a take 4).toList, " which amounts to ", n)

    (n, a drop 4)
  }

  def encrypt(data: (Int, Array[Byte])) = {
    println("About to encrypt ", data)
    val (n, x) = data
    val head = BigInt(n).toByteArray
    val padding = Array.fill[Byte](4 - head.length)(0)

    println("key is ", (padding ++ head).toList, " which amounts to ", n)

    padding ++ head ++ x
  }
}