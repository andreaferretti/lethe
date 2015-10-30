package unicredit.oram

import scala.concurrent.{ Future, ExecutionContext }


class MemoryRemote(slots: Int)(implicit ec: ExecutionContext) extends Remote {
  var data: Array[Array[Byte]] = Array.fill(slots)(Array())

  def capacity = Future(slots)

  def fetch(n: Int) = Future(data(n))

  def put(n: Int, a: Array[Byte]) = Future{ data(n) = a }
}