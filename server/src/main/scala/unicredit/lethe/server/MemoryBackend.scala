package unicredit.lethe.server


class MemoryBackend(val capacity: Int) extends Backend {
  var data = Array.fill(capacity)(Array.empty[Byte])

  override def fetch(n: Int) = data(n)
  override def put(n: Int, a: Array[Byte]) = { data(n) = a }
  override def init(d: Seq[Array[Byte]], start: Int) = {
    if (start + d.length > capacity) {
      throw new Exception("Exceeding capacity")
    }
    for (i <- 0 until d.length) {
      data(i + start) = d(i)
    }
  }
}