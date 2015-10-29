package unicredit.oram

import scala.concurrent.{ Future, ExecutionContext }


class MemoryRemote(var data: Array[Array[Byte]])(implicit ec: ExecutionContext) extends Remote {
  val capacity = Future(data.length)

  def fetch(n: Int) = Future(data(n))

  def put(n: Int, a: Array[Byte]) = Future{ data(n) = a }
}