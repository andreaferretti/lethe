/* Copyright 2016-2019 UniCredit S.p.A.
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
package unicredit.lethe.serialization

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