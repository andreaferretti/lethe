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
package unicredit.lethe

import scala.io.StdIn
import java.security.SecureRandom

import boopickle.Default._

import data._
import oram._
import transport._
import client._


object Save extends App {
  implicit val pstring = Pointed("")

  val remote = ZMQRemote("tcp://localhost:8888")
  val params = Params(depth = 8, bucketSize = 4)
  val oram = PathORAM[String, String, String, String](remote, "Hello my friend", params)

  oram.init
  oram.write("1", "Alice")
  oram.write("2", "Bob")
  oram.write("3", "Eve")
  oram.write("4", "Mallory")

  val path = "test.oram"
  Persistence.save(path, oram)
}