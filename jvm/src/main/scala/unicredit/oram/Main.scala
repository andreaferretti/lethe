package unicredit.oram

import sync._


object Main extends App {
  def flip[A, B](x: (A, B)) = (x._2, x._1)

  val elements = (List.fill(50)(List(
      "this is a secret",
      "this is secret as well",
      "strictly confidential",
      "cippa lippa"
    ))).flatten.zipWithIndex map flip
  val remote = new ZMQRemote("tcp://localhost:8888")
  val oram = new RecursivePathORAM(remote, "Hello world")

  oram.init
  for ((id, doc) <- elements) {
    oram.write(id, doc)
  }
  println("at pos 2:", oram.read(2))
  println("at pos 0:", oram.read(0))
  println("writing new item at pos 2")
  oram.write(2, "new secret")
  println("at pos 2:", oram.read(2))
  println("at pos 125:", oram.read(125))
  println("at pos 525:", oram.read(525))
}