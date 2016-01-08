package unicredit.lethe.server


trait Backend {
  def capacity: Int
  def fetch(n: Int): Array[Byte]
  def put(n: Int, a: Array[Byte]): Unit
  def init(d: Seq[Array[Byte]], start: Int): Unit
}