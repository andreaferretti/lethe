package unicredit.oram
package sync

import java.security.SecureRandom

import boopickle.Default._

import transport.Remote


class PathORAM(remote: Remote, passPhrase: String)
  extends LocalPathORAMProtocol[Int, String] {

    val rng = new SecureRandom

  // implicit val pickle = generatePickler[(Int, String)]
  val client = StandardClient[(Int, String)](remote, passPhrase)
  def empty = ""
  def emptyID = -1
  val L = 8
  val Z = 4
}