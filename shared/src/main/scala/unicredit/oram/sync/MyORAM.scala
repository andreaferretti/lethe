package unicredit.oram.sync

import boopickle.Default._


class MyORAM(val remote: Remote, val passPhrase: String)
  extends AESClient[Int, String] with TrivialORAM[Int, String] {
    implicit val pickle = generatePickler[(Int, String)]

    val empty = ""
}