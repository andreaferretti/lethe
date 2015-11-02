package unicredit.oram.sync


class MemoryRemote extends Remote {
  var data: Array[Array[Byte]] = Array()

  def capacity = data.length

  def fetch(n: Int) = data(n)

  def put(n: Int, a: Array[Byte]) = { data(n) = a }

  def init(d: Seq[Array[Byte]]) = { data = d.toArray }
}