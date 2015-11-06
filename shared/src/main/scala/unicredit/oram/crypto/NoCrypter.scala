package unicredit.oram.crypto


class NoCrypter extends Crypter {
  override def encrypt(a: Array[Byte]) = a
  override def decrypt(a: Array[Byte]) = a
}