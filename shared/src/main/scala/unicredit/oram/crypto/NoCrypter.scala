package unicredit.oram
package crypto

import serialization.Serializer


class NoCrypter extends Crypter {
  override def encrypt(a: Array[Byte]) = a
  override def decrypt(a: Array[Byte]) = a

  type Material = Unit
  override def serialize(implicit s: Serializer[Material]) = Array()
}