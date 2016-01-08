package unicredit.lethe

import boopickle.Default._
import java.nio.ByteBuffer


sealed trait Message

case class Capacity() extends Message
case class Fetch(n: Int) extends Message
case class Put(n: Int, doc: Array[Byte]) extends Message
case class Init(data: Seq[Array[Byte]], start: Int) extends Message


object Message {
  def toBytes(m: Message): Array[Byte] = {
    val buffer = Pickle.intoBytes(m)
    val result = Array.fill[Byte](buffer.limit)(0)
    buffer.clear
    buffer.get(result)
    result
  }

  def fromBytes(xs: Array[Byte]): Message =
    Unpickle[Message].fromBytes(ByteBuffer.wrap(xs))

  implicit class RichMessage(val m: Message) extends AnyVal {
    def toBytes = Message.toBytes(m)
  }

  implicit val pickler: Pickler[Message] = generatePickler[Message]
}