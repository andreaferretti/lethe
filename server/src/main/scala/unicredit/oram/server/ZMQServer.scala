package unicredit.oram
package server

import scala.util.Try

import org.zeromq.ZMQ


object ZMQServer extends App {
  val port = Try(args(0).toInt) getOrElse 8888
  val url = s"tcp://*:$port"
  val context = ZMQ.context(1)
  val socket = context.socket(ZMQ.REP)

  socket.bind(url)
  println(s"Server ready on port $port")

  val backend = new MemoryBackend

  while (true) {
    Message.fromBytes(socket.recv) match {
      case Capacity() => socket.send(Bytes(backend.capacity))
      case Fetch(n) => socket.send(backend.fetch(n))
      case Put(n, doc) =>
        backend.put(n, doc)
        socket.send("ok")
      case Init(data) =>
        backend.init(data)
        socket.send("ok")
    }
  }
}