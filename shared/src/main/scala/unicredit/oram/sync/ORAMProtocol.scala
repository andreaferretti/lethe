package unicredit.oram.sync


trait ORAMProtocol[Id, Doc] {
  def empty: Doc
  def read(id: Id): Doc
  def write(id: Id, doc: Doc): Unit
}