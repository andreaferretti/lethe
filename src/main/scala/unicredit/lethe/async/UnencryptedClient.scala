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
import scala.math.BigInt


trait UnencryptedClient extends Client[Int, String] {

  def decrypt(a: Array[Byte]) = {
    val n = BigInt(a take 4).toInt

    (n, new String(a drop 4, "UTF-8"))
  }

  def encrypt(data: (Int, String)) = {
    val (n, doc) = data
    val head = BigInt(n).toByteArray
    val padding = Array.fill[Byte](4 - head.length)(0)

    padding ++ head ++ doc.getBytes("UTF-8")
  }
}