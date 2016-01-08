package unicredit.lethe

import scala.io.StdIn

import better.files._, Cmds._
import com.github.tototoshi.csv._
import boopickle.Default._

import sync._
import data._
import transport._
import client._


case class Person(
  firstName: String = "",
  lastName: String = "",
  companyName: String = "",
  address: String = "",
  city: String = "",
  county: String = "",
  state: String = "",
  zip: String = "",
  phone1: String = "",
  phone2: String = "",
  email: String = "",
  web: String = ""
)

object Data extends App {
  implicit val pstring = Pointed("")
  implicit val pperson = Pointed(Person())

  val store = DataStore[Person, String, String](
    _.firstName,
    _.city,
    remote = ZMQRemote("tcp://localhost:8888"),
    passPhrase = "Hello my friend",
    params = Params(depth = 8, bucketSize = 4)
  )


  for (document <- ls(file"people")) {
    println(s"Adding document $document")
    val reader = CSVReader.open(document.toJava)
    for (list <- reader) {
      val Seq(l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12) = list
      val person = Person(l1, l2, l3, l4, l5, l6, l7, l8, l9, l10, l11, l12)
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
    println("Lookup a person by city:")
    val city = StdIn.readLine.trim
    if (city == "") { keepGoing = false }
    {
      val people = store.search2(city)
      for (person <- people) {
        println(person)
      }
    }
  }
}