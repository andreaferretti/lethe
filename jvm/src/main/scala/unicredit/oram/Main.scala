package unicredit.oram

import sync._


object Main extends App {
  def flip[A, B](x: (A, B)) = (x._2, x._1)

  val elements = List(
      "this is a secret",
      "this is secret as well",
      "strictly confidential"
    ).zipWithIndex map flip
  // val remote = new MemoryRemote
  val remote = new ZMQRemote("tcp://localhost:8888")
  // val oram = new UnsafeORAM(remote)
  val oram = new MyORAM(remote, "Hello world")

  oram.init(elements)
  println("at pos 2:", oram.read(2))
  println("at pos 0:", oram.read(0))
  println("writing new item at pos 2")
  oram.write(2, "new secret")
  println("at pos 2:", oram.read(2))
}