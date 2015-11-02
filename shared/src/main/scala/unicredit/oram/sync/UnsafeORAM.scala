package unicredit.oram.sync

import boopickle.Default._


class UnsafeORAM(val remote: Remote)
  extends UnencryptedClient[Int, String] with TrivialORAM[Int, String] {
    implicit val pickle = generatePickler[(Int, String)]

    val empty = ""
}