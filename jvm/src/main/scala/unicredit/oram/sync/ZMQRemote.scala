package unicredit.oram
package sync

import org.zeromq.ZMQ


class ZMQRemote(url: String) extends Remote {
  val context = ZMQ.context(1)
  val socket = context.socket(ZMQ.REQ)

  socket.connect(url)

  private def ask(m: ZMQMessage) = {
    socket.send(m.toBytes)
    socket.recv
  }

  private def tell(m: ZMQMessage) = { ask(m); () }

  def capacity = Bytes.toInt(ask(Capacity))

  def fetch(n: Int) = ask(Fetch(n))

  def put(n: Int, a: Array[Byte]) = tell(Put(n, a))

  def init(d: Seq[Array[Byte]]) = tell(Init(d))
}