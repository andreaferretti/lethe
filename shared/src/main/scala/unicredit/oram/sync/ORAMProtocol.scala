package unicredit.oram.sync


trait ORAMProtocol[Id, Doc] {
  def empty: Doc
  def readAndRemove(id: Id): Doc
  def add(id: Id, doc: Doc): Unit

  def read(id: Id) = {
    val data = readAndRemove(id)
    add(id, data)

    data
  }

  def write(id: Id, doc: Doc) = {
    readAndRemove(id)
    add(id, doc)
  }
}