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

import scala.io.StdIn

import better.files._, Dsl._
import com.github.tototoshi.csv._
import boopickle.Default._

import sync._
import data._
import transport._
import client._


case class Person(
  person_ID: String = "",
  name: String = "",
  first: String = "",
  last: String = "",
  middle: String = "",
  email: String = "",
  phone: String = "",
  fax: String = "",
  title: String = ""
)

object Data extends App {
  implicit val pstring = Pointed("")
  implicit val pperson = Pointed(Person())

  val store = DataStore[Person, String, String](
    _.first,
    _.email,
    remote = ZMQRemote("tcp://localhost:8888"),
    passPhrase = "Hello my friend",
    params = Params(depth = 8, bucketSize = 4)
  )


  for (document <- ls(file"people")) {
    println(s"Adding document $document")
    val reader = CSVReader.open(document.toJava)
    for (list <- reader) {
      val Seq(l1, l2, l3, l4, l5, l6, l7, l8, l9) = list
      val person = Person(l1, l2, l3, l4, l5, l6, l7, l8, l9)
      store.add(person)
    }
    reader.close()
  }


  var keepGoing = true
  while (keepGoing) {
    println("Lookup a person by name:")
    val name = StdIn.readLine.trim
    if (name == "") { keepGoing = false }
    else {
      val people = store.search1(name)
      for (person <- people) {
        println(person)
      }
    }
    println("Lookup a person by email:")
    val email = StdIn.readLine.trim
    if (email == "") { keepGoing = false }
    {
      val people = store.search2(email)
      for (person <- people) {
        println(person)
      }
    }
  }
}