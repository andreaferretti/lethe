package unicredit.lethe.async

import scala.concurrent.{ Future, ExecutionContext }


class MemoryRemote(implicit ec: ExecutionContext) extends Remote {
  var data: Array[Array[Byte]] = Array()

  def capacity = Future(data.length)

  def fetch(n: Int) = Future(data(n))

  def put(n: Int, a: Array[Byte]) = Future{ data(n) = a }

  def init(d: Seq[Array[Byte]]) = Future{ data = d.toArray }
}