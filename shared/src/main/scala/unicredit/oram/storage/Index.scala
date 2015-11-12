package unicredit.oram
package storage


trait Index[Id] {
  def getPosition(id: Id): Path
  def putPosition(id: Id, path: Path): Unit
}