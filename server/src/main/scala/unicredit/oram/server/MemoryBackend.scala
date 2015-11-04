package unicredit.oram.server


class MemoryBackend extends Backend {
  var data = Array.empty[Array[Byte]]

  override def capacity = data.length
  override def fetch(n: Int) = data(n)
  override def put(n: Int, a: Array[Byte]) = { data(n) = a }
  override def init(d: Seq[Array[Byte]]) = { data = d.toArray }
}