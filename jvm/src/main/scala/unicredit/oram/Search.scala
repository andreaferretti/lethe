package unicredit.oram

import scala.io.StdIn
import java.util.UUID
import java.security.SecureRandom

import better.files._, Cmds._
import boopickle.Default._

import sync._
import search._
import transport._
import client._


object Search extends App {
  implicit val uuidPickler = transformPickler[UUID, String](_.toString, UUID.fromString)

  val (index, oram) = MultiORAM.pair[
    String,
    Set[UUID],
    UUID,
    String
  ](
    remote = ZMQRemote("tcp://localhost:8888"),
    passPhrase = "Hello my friend",
    emptyID = "",
    empty = Set.empty[UUID],
    emptyID1 = UUID.fromString("16b01bbe-484b-49e8-85c5-f424a983205f"),
    empty1 = "",
    L = 8,
    Z = 4)

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

  while (true) {
    println("Lookup a word:")
    val word = StdIn.readLine.trim
    val docs = store.search(word)
    for ((doc, i) <- docs.zipWithIndex) {
      println(s"====Document $i====")
      println(doc.take(200) + "...")
    }
  }
}