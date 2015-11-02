package unicredit.oram
package sync

import org.zeromq.ZMQ


object ZMQServer extends App {
  val url = "tcp://*:8888"
  val context = ZMQ.context(1)
  val socket = context.socket(ZMQ.REP)

  socket.bind(url)
  // socket.subscribe("".getBytes)

  var data = Array.empty[Array[Byte]]

  while (true) {
    val m = Message.fromBytes(socket.recv)
    println(m)
    m match {
      case Capacity() => socket.send(Bytes(data.length))
      case Fetch(n) => socket.send(data(n))
      case Put(n, doc) =>
        data(n) = doc
        socket.send("ok")
      case Init(d) =>
        data = d.toArray
        socket.send("ok")
    }
  }
}