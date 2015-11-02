package unicredit.oram.sync


class UnsafeORAM(val remote: Remote)
  extends UnencryptedClient with TrivialORAM[Int, String] {
    val empty = ""
}