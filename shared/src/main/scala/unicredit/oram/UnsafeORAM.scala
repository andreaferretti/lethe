package unicredit.oram

import scala.concurrent.ExecutionContext


class UnsafeORAM(val remote: Remote)(implicit val ec: ExecutionContext)
  extends UnencryptedClient with TrivialORAM[Int, String] {
    val empty = ""
}