package unicredit.oram

import minitest._
import boopickle.Default._

import sync._
import transport.MemoryRemote


object ORAMSpec extends SimpleTestSuite {

  test("The local path ORAM should be able to write and retrieve keys") {
    def flip[A, B](x: (A, B)) = (x._2, x._1)

    val elements = (List.fill(50)(List(
        "this is a secret",
        "this is secret as well",
        "strictly confidential",
        "cippa lippa"
      ))).flatten.zipWithIndex map flip
    implicit val pint = Pointed(-1)
    implicit val pstring = Pointed("")
    val params = Params(depth = 8, bucketSize = 4)
    val remote = new MemoryRemote(capacity = params.numSlots)
    val oram = PathORAM[Int, String, Int, String](remote, "Hello world", params)

    oram.init
    for ((id, doc) <- elements) {
      oram.write(id, doc)
    }
    assertEquals(oram.read(2), "strictly confidential")
    assertEquals(oram.read(0), "this is a secret")

    oram.write(2, "new secret")

    assertEquals(oram.read(2), "new secret")
    assertEquals(oram.read(125), "this is secret as well")
    assertEquals(oram.read(525), "")
  }
}