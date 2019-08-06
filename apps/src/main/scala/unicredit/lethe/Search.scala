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
import java.util.UUID
import java.security.SecureRandom

import better.files._, Dsl._
import boopickle.Default._

import client._
import oram._
import search._
import transport._


object Search extends App {

  implicit val pint = Pointed(-1)
  implicit val pstring = Pointed("")
  implicit val puuid = Pointed(UUID.fromString("16b01bbe-484b-49e8-85c5-f424a983205f"))
  implicit val puuidset = Pointed(Set.empty[UUID])

  val (index, oram) = MultiORAM.gen2[String, Set[UUID], UUID, String](
    remote = ZMQRemote("tcp://localhost:8888"),
    passPhrase = "Hello my friend",
    params = Params(depth = 8, bucketSize = 4)
  )

  val store = new BasicStore(index, oram)

  println("Starting initialization...")
  index.init
  oram.init
  println("Done!")

  // for (document <- ls(file"examples")) {
  //   println(s"Adding document $document")
  //   store.addDocument(document.contentAsString)
  // }
  println("Adding documents...")
  val documents = ls(file"examples").map(_.contentAsString).toList
  store.addDocuments(documents)
  println("Done!")

  var keepGoing = true
  while (keepGoing) {
    println("Lookup a word:")
    val word = StdIn.readLine.trim
    if (word == "") { keepGoing = false }
    else {
      val docs = store.search(word)
      for ((doc, i) <- docs.zipWithIndex) {
        println(s"====Document $i====")
        println(doc.take(200) + "...")
      }
    }
  }
}