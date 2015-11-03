package unicredit.oram.sync


trait TrivialORAMProtocol[Id, Doc] extends ORAMProtocol[Id, Doc] { self: Client[Id, Doc] =>
  // Note: There is no need to express read and write in terms of
  // readAndRemove and add
  override def read(id: Id) = {
    val data = readAndRemove(id)
    add(id, data)

    data
  }

  override def write(id: Id, doc: Doc) = {
    readAndRemove(id)
    add(id, doc)
  }

  def readAndRemove(id: Id) = {
    val n = remote.capacity
    var result: Doc = empty

    for (i <- 0 until n) {
      val (uid, doc) = fetchClear(i)
      if (id == uid) {
        result = doc
      }
      val data = if (id == uid) (uid, empty) else (uid, doc)
      putClear(i, data)
    }

    result
  }

  def add(id: Id, doc: Doc) = {
    val n = remote.capacity

    for (i <- 0 until n) {
      val (uid, udoc) = fetchClear(i)
      val data = if (id == uid) (uid, doc) else (uid, udoc)
      putClear(i, data)
    }
  }
}