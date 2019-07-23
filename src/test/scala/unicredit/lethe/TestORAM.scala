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

import minitest._
import boopickle.Default._

import sync._
import transport.MemoryRemote


object ORAMSpec extends SimpleTestSuite {
  val elements = List(
    "this is a secret",
    "this is secret as well",
    "strictly confidential",
    "cippa lippa"
  )

  implicit val pint = Pointed(-1)
  implicit val pstring = Pointed("")
  val params = Params(depth = 8, bucketSize = 4)

  def localOram(passPhrase: String) = {
    val remote = new MemoryRemote(capacity = params.numSlots)
    PathORAM[Int, String, Int, String](remote, passPhrase, params)
  }
  def trivialOram(passPhrase: String) = {
    val remote = new MemoryRemote(capacity = params.numSlots)
    TrivialORAM.unsafe[Int, String](remote)
  }

  // test("The trivial path ORAM should be able to write and retrieve keys") {
  //   val oram = trivialOram("Hello world")
  //
  //   oram.init
  //   for ((doc, id) <- elements.zipWithIndex) {
  //     oram.write(id, doc)
  //   }
  //   assertEquals(oram.read(2), "strictly confidential")
  //   assertEquals(oram.read(0), "this is a secret")
  // }
  //
  // test("A missing key should result in an empty doc") {
  //   val oram = trivialOram("Hello world")
  //
  //   oram.init
  //   for ((doc, id) <- elements.zipWithIndex) {
  //     oram.write(id, doc)
  //   }
  //   assertEquals(oram.read(12), "")
  // }
  //
  // test("The trivial path ORAM should be able to overwrite keys") {
  //   val oram = trivialOram("Hello world")
  //
  //   oram.init
  //   for ((doc, id) <- elements.zipWithIndex) {
  //     oram.write(id, doc)
  //   }
  //   assertEquals(oram.read(2), "strictly confidential")
  //   oram.write(2, "new secret")
  //   assertEquals(oram.read(2), "new secret")
  // }

  test("The local path ORAM should be able to write and retrieve keys") {
    val oram = localOram("Hello world")

    oram.init
    for ((doc, id) <- elements.zipWithIndex) {
      oram.write(id, doc)
    }
    assertEquals(oram.read(2), "strictly confidential")
    assertEquals(oram.read(0), "this is a secret")
  }

  test("A missing key should result in an empty doc") {
    val oram = localOram("Hello world")

    oram.init
    for ((doc, id) <- elements.zipWithIndex) {
      oram.write(id, doc)
    }
    assertEquals(oram.read(12), "")
  }

  test("The local path ORAM should be able to overwrite keys") {
    val oram = localOram("Hello world")

    oram.init
    for ((doc, id) <- elements.zipWithIndex) {
      oram.write(id, doc)
    }
    assertEquals(oram.read(2), "strictly confidential")
    oram.write(2, "new secret")
    assertEquals(oram.read(2), "new secret")
  }
}