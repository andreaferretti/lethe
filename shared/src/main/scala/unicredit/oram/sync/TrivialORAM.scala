package unicredit.oram.sync


trait TrivialORAM[Id, Doc] extends ORAM[Id, Doc] with Client[Id, Doc] {
  override def readAndRemove(id: Id) = {
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

  override def add(id: Id, doc: Doc) = {
    val n = remote.capacity

    for (i <- 0 until n) {
      val (uid, udoc) = fetchClear(i)
      val data = if (id == uid) (uid, doc) else (uid, udoc)
      putClear(i, data)
    }
  }
}