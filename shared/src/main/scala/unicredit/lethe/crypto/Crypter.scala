package unicredit.lethe
package crypto

import serialization.Serializer


trait Crypter {
  def encrypt(a: Array[Byte]): Array[Byte]
  def decrypt(a: Array[Byte]): Array[Byte]
  type Material
  def serialize(implicit s: Serializer[Material]): Array[Byte]
}