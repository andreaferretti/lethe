package unicredit.oram

import scala.concurrent.Future


trait Remote {
  def capacity: Future[Int]

  def fetch(n: Int): Future[Array[Byte]]

  def put(n: Int, a: Array[Byte]): Future[Unit]
}