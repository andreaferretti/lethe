package unicredit.oram.sync

import scala.math.BigInt
import java.nio.ByteBuffer

import boopickle.Default._


trait UnencryptedClient[Id, Doc] extends BasicClient[Id, Doc] {
  def decryptBytes(a: Array[Byte]) = a
  def encryptBytes(a: Array[Byte]) = a
}