package unicredit.oram.serialization

import java.nio.ByteBuffer

import boopickle.Default._


class BooSerializer[A](implicit pickler: Pickler[A]) extends Serializer[A] {
  override def decode(a: Array[Byte]) =
    Unpickle[A].fromBytes(ByteBuffer.wrap(a))

  override def encode(data: A) = {
    val buffer = Pickle.intoBytes(data)
    val result = Array.fill[Byte](buffer.limit)(0)
    buffer.clear
    buffer.get(result)
    result
  }
}