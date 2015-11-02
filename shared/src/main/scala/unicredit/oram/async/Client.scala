package unicredit.oram.async

import scala.concurrent.{ Future, ExecutionContext }


trait Client[Id, Doc] {
  implicit def ec: ExecutionContext

  def remote: Remote

  def decrypt(a: Array[Byte]): (Id, Doc)

  def encrypt(data: (Id, Doc)): Array[Byte]

  def fetchClear(n: Int) = remote.fetch(n) map decrypt

  def putClear(n: Int, data: (Id, Doc)) = remote.put(n, encrypt(data))

  def init(data: Seq[(Id, Doc)]) = remote.init(data map encrypt)
}