package unicredit.oram

import boopickle.Default._

import sync._
import transport.ZMQRemote


object Main extends App {
  def flip[A, B](x: (A, B)) = (x._2, x._1)

  val elements = (List.fill(50)(List(
      "this is a secret",
      "this is secret as well",
      "strictly confidential",
      "cippa lippa"
    ))).flatten.zipWithIndex map flip
  val remote = new ZMQRemote("tcp://localhost:8888")
  implicit val pint = Pointed(-1)
  implicit val pstring = Pointed("")
  val oram = PathORAM.recursive[Int, String, Int](
    remote, "Hello world", 8, 4, 0, (_: Int) % 1024)

  println("starting initialization...")
  oram.init
  println("done!")
  println("writing 200 elements...")
  for ((id, doc) <- elements) {
    oram.write(id, doc)
  }
  println("done!")
  println("at pos 2:", oram.read(2))
  println("at pos 0:", oram.read(0))
  println("writing new item at pos 2")
  oram.write(2, "new secret")
  println("at pos 2:", oram.read(2))
  println("at pos 125:", oram.read(125))
  println("at pos 525:", oram.read(525))
}