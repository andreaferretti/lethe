package unicredit.oram.async

import scala.concurrent.Future


trait Remote {
  def capacity: Future[Int]

  def fetch(n: Int): Future[Array[Byte]]

  def put(n: Int, a: Array[Byte]): Future[Unit]

  def init(a: Seq[Array[Byte]]): Future[Unit]
}