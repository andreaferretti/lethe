package unicredit.oram
package sync

import boopickle.Default._


class RecursivePathORAM(val remote: Remote, val passPhrase: String)
  extends RecursivePathORAMProtocol[Int, String, Int] with AESClient[Int, String] {

  implicit val pickleId = implicitly[Pickler[Int]]
  implicit val pickleBin = implicitly[Pickler[Int]]
  implicit val pickle = generatePickler[(Int, String)]

  def bin(x: Int) = x % 1024

  def empty = ""
  def emptyID = -1
  def emptyBin = -1
  val L = 8
  val Z = 4
}