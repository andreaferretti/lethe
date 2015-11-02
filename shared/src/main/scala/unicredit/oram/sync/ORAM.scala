package unicredit.oram.sync


trait ORAM[Id, Doc] {
  def empty: Doc
  def readAndRemove(id: Id): Option[Doc]
  def add(id: Id, doc: Doc): Unit

  def read(id: Id) = {
    val data = readAndRemove(id)
    add(id, data getOrElse empty)

    data
  }

  def write(id: Id, doc: Doc) = {
    readAndRemove(id)
    add(id, doc)
  }
}