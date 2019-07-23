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