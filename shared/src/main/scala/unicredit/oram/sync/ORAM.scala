package unicredit.oram.sync


trait ORAM[Id, Doc] {
  def read(id: Id): Doc
  def write(id: Id, doc: Doc): Unit
  def init: Unit
}