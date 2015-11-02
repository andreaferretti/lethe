package unicredit.oram
package sync


trait ZMQMessage

case object Capacity extends ZMQMessage
case class Fetch(n: Int) extends ZMQMessage
case class Put(n: Int, doc: Array[Byte]) extends ZMQMessage
case class Init(data: Seq[Array[Byte]]) extends ZMQMessage


object ZMQMessage {
  def toBytes(m: ZMQMessage): Array[Byte] = m match {
    case Capacity => Bytes("cap")
    case Fetch(n) => Bytes("ftc") ++ Bytes(n)
    case Put(n, doc) => Bytes("put") ++ Bytes(n) ++ doc
    case Init(data) => Bytes("ini") ++ Bytes(data)
  }


  def fromBytes(xs: Array[Byte]): ZMQMessage = Bytes.toString(xs take 3) match {
    case "cap" => Capacity
    case "ftc" => Fetch(Bytes.toInt(xs drop 3))
    case "put" =>
      val n = Bytes.toInt(xs drop 3 take 4)
      Put(n, xs drop 7)
    case "ini" =>
      Init(Bytes.toArraySeq(xs drop 3))
    case _ => ???
  }

  implicit class RichMessage(val m: ZMQMessage) extends AnyVal {
    def toBytes = ZMQMessage.toBytes(m)
  }
}