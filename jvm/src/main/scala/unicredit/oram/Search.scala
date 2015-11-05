package unicredit.oram

import scala.io.StdIn
import java.util.UUID

import better.files._, Cmds._
import boopickle.Default._

import sync._
import search._


object Search extends App {
  implicit val uuidPickler = transformPickler[UUID, String](_.toString, UUID.fromString)

  class IndexORAM(val remote: Remote, val passPhrase: String)
    extends LocalPathORAMProtocol[String, Set[UUID]]
    with AESClient[String, Set[UUID]] {
      val L = 8
      val Z = 4
      val empty = Set.empty[UUID]
      val emptyID = ""
      val pickle = generatePickler[(String, Set[UUID])]
    }

  class DocumentORAM(val remote: Remote, val passPhrase: String)
    extends LocalPathORAMProtocol[UUID, String]
    with AESClient[UUID, String] {
      val L = 8
      val Z = 4
      val empty = ""
      val emptyID = UUID.fromString("16b01bbe-484b-49e8-85c5-f424a983205f")
      val pickle = generatePickler[(UUID, String)]
    }

  val remote = new ZMQRemote("tcp://localhost:8888")
  val index = new IndexORAM(remote, "Hello world")
  val oram = new DocumentORAM(remote, "Hello world")

  val store = new BasicStore(index, oram)

  println("Starting initialization...")
  oram.init
  println("Done!")

  // val Directory(d) = file"examples"
  for (document <- ls(file"examples")) {
    println(s"Adding document $document")
    store.addDocument(document.contentAsString)
  }
  println("Done!")

  while (true) {
    println("Lookup a word:")
    val word = StdIn.readLine.trim
    val docs = store.search(word)
    for (doc <- docs) {
      println(doc.take(200) + "...")
    }
  }
}