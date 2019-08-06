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

import boopickle.Default._

import client._
import data._
import oram._
import transport._


object Basic extends App {
  implicit val pstring = Pointed("")

  val remote = new MemoryRemote(capacity = 100000)
  val params = Params(depth = 8, bucketSize = 4)
  val oram = PathORAM[String, String, String, String](remote, "Hello my friend", params)

  oram.init
  oram.write("1", "Alice")
  oram.write("2", "Bob")
  oram.write("3", "Eve")
  oram.write("4", "Mallory")


  var keepGoing = true
  while (keepGoing) {
    println("Choose a number between 1 and 4:")
    val input = StdIn.readLine.trim
    try {
      val n = input.toInt
      if ((n >= 1) && (n <= 4)) {
        val name = oram.read(n.toString)
        println(name)
      }
    }
    catch {
      case _: Throwable =>
        if ((input == "q") || (input == "quit") || (input == "x") || (input == "exit")) {
          keepGoing = false
        }
    }
  }
}