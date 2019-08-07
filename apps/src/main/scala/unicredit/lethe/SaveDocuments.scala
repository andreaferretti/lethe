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

import java.util.UUID
import java.nio.charset.Charset
import java.security.SecureRandom

import better.files._, Dsl._
import boopickle.Default._

import client._
import oram._
import search._
import transport._


object SaveDocuments extends App {
  implicit val pint = Pointed(-1)
  implicit val pstring = Pointed("")
  implicit val puuid = Pointed(UUID.fromString("16b01bbe-484b-49e8-85c5-f424a983205f"))
  implicit val puuidset = Pointed(Set.empty[UUID])
  implicit val rng = new SecureRandom

  val multiOramGen = new MultiORAM2[String, Set[UUID], UUID, String]
  val (index, oram) = multiOramGen.gen(
    remote = ZMQRemote("tcp://localhost:8888"),
    passPhrase = "Hello my friend",
    params = Params(depth = 16, bucketSize = 4)
  )

  val store = new BasicStore(index, oram)

  val wikipedia = file"wiki-tokenized.txt"
  val n = 20
  val utf8 = Charset.forName("UTF-8")

  var numLines = 0
  for (_ <- wikipedia.lineIterator(charset=utf8)) {
    numLines += 1
  }
  println(s"The file contains $numLines lines")


  println("Starting initialization...")
  index.init
  oram.init
  println("Done!")

  println("Adding documents...")
  var count = 0
  for (documents <- wikipedia.lineIterator(charset=utf8).sliding(n, n)) {
    store.addDocuments(documents)
    count += 1
    print(s"${count * 20}/$numLines\r")
  }
  println()
  println("Done!")

  Persistence.save("data.oram", oram)
  Persistence.save("index.oram", index)
}