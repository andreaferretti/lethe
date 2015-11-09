package unicredit.oram
package crypto

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

class AESCrypter(material: AESMaterial) extends Crypter {
  type Material = AESMaterial
  val rng = new SecureRandom()

  def getRandomBytes(size: Int) = {
    val bytes = Array.ofDim[Byte](size)
    rng.nextBytes(bytes)
    bytes
  }

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
    ecipher.doFinal(getRandomBytes(prefixLength) ++ content ++ padding)
  }

  override def serialize(implicit s: Serializer[AESMaterial]) =
    s.encode(material)
}

object AESCrypter {
  private val ITERATIONS = 65536
  private val KEY_LENGTH = 256
  private val rng = new SecureRandom()
  private val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

  def getRandomBytes(size: Int) = {
    val bytes = Array.ofDim[Byte](size)
    rng.nextBytes(bytes)
    bytes
  }

  def apply(material: AESMaterial): AESCrypter =
    new AESCrypter(material)

  def apply(passPhrase: String): AESCrypter = {
    val salt = getRandomBytes(64)
    val spec = new PBEKeySpec(passPhrase.toCharArray, salt, ITERATIONS, KEY_LENGTH)
    val ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    val iv = ecipher.getParameters.getParameterSpec(classOf[IvParameterSpec]).getIV

    apply(AESMaterial(iv, factory.generateSecret(spec).getEncoded, "AES"))
  }
}