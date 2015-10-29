package unicredit.oram

import scala.concurrent.{ Future, ExecutionContext }
import scala.math.BigInt


class UnencryptedClient(val remote: Remote)(implicit val ec: ExecutionContext)
  extends Client[Int, Array[Byte]] {

  def decrypt(a: Array[Byte]) = {
    val n = BigInt(a take 4).toInt

    (n, a drop 4)
  }

  def encrypt(data: (Int, Array[Byte])) = {
    val (n, x) = data
    val head = BigInt(n).toByteArray
    val padding = Array.fill[Byte](4 - head.length)(0)

    padding ++ head ++ x
  }
}