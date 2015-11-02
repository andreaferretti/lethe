package unicredit.oram.sync

import scala.math.BigInt
import java.nio.ByteBuffer

import boopickle.Default._


trait UnencryptedClient[Id, Doc] extends Client[Id, Doc] {
  implicit def pickle: Pickler[(Id, Doc)]

  def decrypt(a: Array[Byte]) =
    Unpickle[(Id, Doc)].fromBytes(ByteBuffer.wrap(a))

  def encrypt(data: (Id, Doc)) = {
    val buffer = Pickle.intoBytes(data)
    val result = Array.fill[Byte](buffer.limit)(0)
    buffer.clear
    buffer.get(result)
    result
  }
}