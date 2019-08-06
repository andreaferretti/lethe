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
package unicredit.lethe.transport


class MemoryRemote(val capacity: Int) extends Remote {
  var data = Array.fill(capacity)(Array.empty[Byte])

  def fetch(n: Int) = data(n)

  def put(n: Int, a: Array[Byte]) = { data(n) = a }

  def init(d: Seq[Array[Byte]], start: Int) = {
    if (start + d.length > capacity) {
      throw new Exception("Exceeding capacity")
    }
    for (i <- 0 until d.length) {
      data(i + start) = d(i)
    }
  }
}