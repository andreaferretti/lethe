package unicredit.oram
package sync


trait LocalPathORAMProtocol[Id, Doc] extends PathORAMProtocol[Id, Doc] {
  var position = Map.empty[Id, Path]

  override def getPosition(id: Id) = position.getOrElse(id, Path.random(L))

  override def putPosition(id: Id, path: Path) = {
    position += (id -> path)
  }
}