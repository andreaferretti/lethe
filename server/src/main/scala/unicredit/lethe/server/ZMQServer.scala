/* Copyright 2016 UniCredit S.p.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package unicredit.lethe
package server

import scala.util.Try
import java.io.File

import org.zeromq.ZMQ

object ZMQServer extends App {
  val port = Try(args(0).toInt) getOrElse 8888
  val url = s"tcp://*:$port"
  val context = ZMQ.context(1)
  val socket = context.socket(ZMQ.REP)

  socket.bind(url)
  println(s"Server ready on port $port")

  // val backend = new MemoryBackend
  val backend = new LevelDBBackend(new File(s"data/db-$port"))

  while (true) {
    Message.fromBytes(socket.recv) match {
      case Capacity() => socket.send(Bytes(backend.capacity))
      case Fetch(n)   => socket.send(backend.fetch(n))
      case Put(n, doc) =>
        backend.put(n, doc)
        socket.send("ok")
      case Init(data, start) =>
        backend.init(data, start)
        socket.send("ok")
    }
  }
}
