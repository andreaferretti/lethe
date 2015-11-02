package unicredit.oram
package sync

import org.zeromq.ZMQ


class ZMQRemote(url: String) extends Remote {
  val context = ZMQ.context(1)
  val socket = context.socket(ZMQ.SUB)

  socket.connect(url)
  socket.subscribe("".getBytes)

  private def ask(m: ZMQMessage) = {
    socket.send(m.toBytes)
    socket.recv
  }

  private def tell(m: ZMQMessage) =
    socket.send(m.toBytes)

  def capacity = Bytes.toInt(ask(Capacity))

  def fetch(n: Int) = ask(Fetch(n))

  def put(n: Int, a: Array[Byte]) = tell(Put(n, a))

  def init(d: Seq[Array[Byte]]) = tell(Init(d))
}