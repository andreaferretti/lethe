package unicredit.oram.sync.transport


trait Remote {
  def capacity: Int

  def fetch(n: Int): Array[Byte]

  def put(n: Int, a: Array[Byte]): Unit

  def init(a: Seq[Array[Byte]]): Unit
}