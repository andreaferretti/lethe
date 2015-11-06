package unicredit.oram.crypto


trait Crypter {
  def encrypt(a: Array[Byte]): Array[Byte]
  def decrypt(a: Array[Byte]): Array[Byte]
}