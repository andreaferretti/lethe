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
package transport

import org.zeromq.ZMQ


class ZMQRemote(url: String) extends Remote {
  val context = ZMQ.context(1)
  val socket = context.socket(ZMQ.REQ)

  socket.connect(url)

  private def ask(m: Message) = {
    socket.send(m.toBytes)
    socket.recv
  }

  private def tell(m: Message) = { ask(m); () }

  def capacity = Bytes.toInt(ask(Capacity()))

  def fetch(n: Int) = ask(Fetch(n))

  def put(n: Int, a: Array[Byte]) = tell(Put(n, a))

  def init(d: Seq[Array[Byte]], start: Int) = tell(Init(d, start))
}

object ZMQRemote {
  def apply(url: String) = new ZMQRemote(url)
}