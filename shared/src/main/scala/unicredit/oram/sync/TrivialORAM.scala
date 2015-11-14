package unicredit.oram
package sync

import client._


class TrivialORAM[Id: Pointed, Doc: Pointed](client: Client[(Id, Doc)]) extends ORAM[Id, Doc] {
  val emptyId = implicitly[Pointed[Id]].empty
  val emptyDoc = implicitly[Pointed[Doc]].empty
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

  override def init =
    for (i <- 0 until client.capacity) {
      client.putClear(i, emptyId -> emptyDoc)
    }

  def readAndRemove(id: Id) = {
    val n = client.capacity
    var result = emptyDoc

    for (i <- 0 until n) {
      val (uid, doc) = client.fetchClear(i)
      if (id == uid) {
        result = doc
      }
      val data = if (id == uid) (uid, emptyDoc) else (uid, doc)
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

  def unsafe[
    Id: Pointed: Pickler,
    Doc: Pointed: Pickler
  ](remote: Remote) =
    new TrivialORAM[Id, Doc](new UnencryptedClient[(Id, Doc)](remote))
}