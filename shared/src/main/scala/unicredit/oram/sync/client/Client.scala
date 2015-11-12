package unicredit.oram.sync.client

trait Client[A] {
  def capacity: Int

  def fetchClear(n: Int): A

  def putClear(n: Int, data: A): Unit

  def init(data: Seq[A], start: Int): Unit
}