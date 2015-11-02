package unicredit.oram.async

import scala.concurrent.ExecutionContext


class UnsafeORAM(val remote: Remote)(implicit val ec: ExecutionContext)
  extends UnencryptedClient with TrivialORAM[Int, String] {
    val empty = ""
}