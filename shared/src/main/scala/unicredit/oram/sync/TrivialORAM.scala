package unicredit.oram.sync

import boopickle.Default._


class TrivialORAM(val remote: Remote, val passPhrase: String)
  extends AESClient[Int, String] with TrivialORAMProtocol[Int, String] {
    implicit val pickle = generatePickler[(Int, String)]

    val empty = ""
}