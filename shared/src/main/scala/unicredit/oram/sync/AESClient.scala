package unicredit.oram.sync

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.{ Cipher, SecretKey, SecretKeyFactory }
import javax.crypto.spec.{ IvParameterSpec, PBEKeySpec, SecretKeySpec }


trait AESClient[Id, Doc] extends BasicClient[Id, Doc] {
  def passPhrase: String
  val ITERATIONS = 65536
  val KEY_LENGTH = 256
  val rnd = new SecureRandom()
  val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")

  def getRandomBytes(size: Int) = {
    val bytes = Array.ofDim[Byte](size)
    val rnd = new SecureRandom()
    rnd.nextBytes(bytes)
    bytes
  }

  val salt = getRandomBytes(64)
  val spec = new PBEKeySpec(passPhrase.toCharArray, salt, ITERATIONS, KEY_LENGTH)
  val secret = new SecretKeySpec(factory.generateSecret(spec).getEncoded, "AES")
  val ecipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
  ecipher.init(Cipher.ENCRYPT_MODE, secret)
  val dcipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
  val iv = ecipher.getParameters.getParameterSpec(classOf[IvParameterSpec]).getIV
  dcipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(iv))

  val messageStride = 1000
  val prefixLength = 128

  override def decrypt(a: Array[Byte]) = {
    val content = dcipher.doFinal(a)
    decode(content drop prefixLength)
  }

  override def encrypt(data: (Id, Doc)) = {
    val content = encode(data)
    // Add a padding so that messages do not leak exact length
    val paddingSize = messageStride - (content.size % messageStride)
    val padding = Array.fill[Byte](paddingSize)(0)
    // Add a random prefix for semantic encryption
    ecipher.doFinal(getRandomBytes(prefixLength) ++ content ++ padding)
  }
}