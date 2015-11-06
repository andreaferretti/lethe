package unicredit.oram.sync


class TrivialORAM[Id, Doc](val client: Client[(Id, Doc)], val empty: Doc) extends ORAM[Id, Doc] {
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
    val n = client.capacity
    var result: Doc = empty

    for (i <- 0 until n) {
      val (uid, doc) = client.fetchClear(i)
      if (id == uid) {
        result = doc
      }
      val data = if (id == uid) (uid, empty) else (uid, doc)
      client.putClear(i, data)
    }

    result
  }

  def add(id: Id, doc: Doc) = {
    val n = client.capacity

    for (i <- 0 until n) {
      val (uid, udoc) = client.fetchClear(i)
      val data = if (id == uid) (uid, doc) else (uid, udoc)
      client.putClear(i, data)
    }
  }
}

object TrivialORAM {
  import boopickle.Default._
  import transport.Remote

  def unsafe[Id, Doc](remote: Remote, empty: Doc)(implicit pickler: Pickler[(Id, Doc)]) =
    new TrivialORAM[Id, Doc](new UnencryptedClient[(Id, Doc)](remote), empty)
}