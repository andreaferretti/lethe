package unicredit.oram.sync.transport


class MemoryRemote(val capacity: Int) extends Remote {
  var data = Array.fill(capacity)(Array.empty[Byte])

  def fetch(n: Int) = data(n)

  def put(n: Int, a: Array[Byte]) = { data(n) = a }

  def init(d: Seq[Array[Byte]], start: Int) = {
    if (start + d.length > capacity) {
      throw new Exception("Exceeding capacity")
    }
    for (i <- 0 until d.length) {
      data(i + start) = d(i)
    }
  }
}