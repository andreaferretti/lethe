/* Copyright 2016 UniCredit S.p.A.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package unicredit.lethe
package crypto

import java.util.Random
import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.{ Cipher, SecretKey, SecretKeyFactory }
import javax.crypto.spec.{ IvParameterSpec, PBEKeySpec, SecretKeySpec }

import serialization.Serializer


case class AESMaterial(
  iv: Array[Byte],
  secret: Array[Byte],
  algorithm: String
)

class AESCrypter(material: AESMaterial, rng: Random) extends Crypter {
  type Material = AESMaterial

  val secret = new SecretKeySpec(material.secret, material.algorithm)
  val ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
  ecipher.init(Cipher.ENCRYPT_MODE, secret, new IvParameterSpec(material.iv))
  val dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
  dcipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(material.iv))

  val messageStride = 1000
  val prefixLength = 128

  override def decrypt(a: Array[Byte]) = {
    val content = dcipher.doFinal(a)
    content drop prefixLength
  }

  override def encrypt(content: Array[Byte]) = {
    // Add a padding so that messages do not leak exact length
    val paddingSize = messageStride - (content.size % messageStride)
    val padding = Array.fill[Byte](paddingSize)(0)
    // Add a random prefix for semantic encryption
    ecipher.doFinal(AESCrypter.randomBytes(prefixLength, rng) ++ content ++ padding)
  }

  override def serialize(implicit s: Serializer[AESMaterial]) =
    s.encode(material)
}

object AESCrypter {
  private val ITERATIONS = 65536
  private val KEY_LENGTH = 256
  private val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

  def randomBytes(size: Int, rng: Random) = {
    val bytes = Array.ofDim[Byte](size)
    rng.nextBytes(bytes)
    bytes
  }

  def apply(material: AESMaterial, rng: Random): AESCrypter =
    new AESCrypter(material, rng)

  def apply(passPhrase: String, rng: Random = new SecureRandom): AESCrypter = {
    val salt = randomBytes(64, rng)
    val spec = new PBEKeySpec(passPhrase.toCharArray, salt, ITERATIONS, KEY_LENGTH)
    val ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val iv = ecipher.getParameters.getParameterSpec(classOf[IvParameterSpec]).getIV

    apply(AESMaterial(iv, factory.generateSecret(spec).getEncoded, "AES"), rng)
  }
}