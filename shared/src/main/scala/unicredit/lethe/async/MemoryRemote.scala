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
package unicredit.lethe.async

import scala.concurrent.{ Future, ExecutionContext }


class MemoryRemote(implicit ec: ExecutionContext) extends Remote {
  var data: Array[Array[Byte]] = Array()

  def capacity = Future(data.length)

  def fetch(n: Int) = Future(data(n))

  def put(n: Int, a: Array[Byte]) = Future{ data(n) = a }

  def init(d: Seq[Array[Byte]]) = Future{ data = d.toArray }
}