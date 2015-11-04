package unicredit.oram.sync

import java.nio.ByteBuffer

import boopickle.Default._


trait BasicClient[Id, Doc] extends Client[Id, Doc] {
  implicit def pickle: Pickler[(Id, Doc)]

  def encryptBytes(a: Array[Byte]): Array[Byte]
  def decryptBytes(a: Array[Byte]): Array[Byte]

  def decode(a: Array[Byte]) =
    Unpickle[(Id, Doc)].fromBytes(ByteBuffer.wrap(a))

  def encode(data: (Id, Doc)) = {
    val buffer = Pickle.intoBytes(data)
    val result = Array.fill[Byte](buffer.limit)(0)
    buffer.clear
    buffer.get(result)
    result
  }

  def decrypt(a: Array[Byte]) = decode(decryptBytes(a))
  def encrypt(a: (Id, Doc)) = encryptBytes(encode(a))
}