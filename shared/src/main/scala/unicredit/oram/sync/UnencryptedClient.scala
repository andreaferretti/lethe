package unicredit.oram.sync

import scala.math.BigInt
import java.nio.ByteBuffer

import boopickle.Default._


trait UnencryptedClient[Id, Doc] extends BasicClient[Id, Doc] {
  def decrypt(a: Array[Byte]) = decode(a)

  def encrypt(data: (Id, Doc)) = encode(data)
}