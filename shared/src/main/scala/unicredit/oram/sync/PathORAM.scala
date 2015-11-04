package unicredit.oram
package sync

import boopickle.Default._


class PathORAM(val remote: Remote, val passPhrase: String)
  extends LocalPathORAMProtocol[Int, String] with AESClient[Int, String] {

  implicit val pickle = generatePickler[(Int, String)]
  def empty = ""
  def emptyID = -1
  val L = 8
  val Z = 4
}