package unicredit.oram.serialization


trait Serializer[A] {
  def encode(a: A): Array[Byte]
  def decode(a: Array[Byte]): A
}