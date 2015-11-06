package unicredit.oram
package sync

import java.security.SecureRandom

import boopickle.Default._

import transport.Remote


class RecursivePathORAM(remote: Remote, passPhrase: String)
  extends RecursivePathORAMProtocol[Int, String, Int] {

  val rng = new SecureRandom

  implicit val pickleId = implicitly[Pickler[Int]]
  implicit val pickleBin = implicitly[Pickler[Int]]
  implicit val pickle = generatePickler[(Int, String)]
  val client = StandardClient[(Int, String)](remote, passPhrase)

  def bin(x: Int) = x % 1024

  def empty = ""
  def emptyID = -1
  def emptyBin = -1
  val L = 8
  val Z = 4
}