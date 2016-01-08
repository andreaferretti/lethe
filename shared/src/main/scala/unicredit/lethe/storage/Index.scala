package unicredit.lethe
package storage


trait Index[Id] {
  def getPosition(id: Id): Path
  def putRandom(id: Id): Unit
  def init: Unit
  def serialize: Array[Byte]
}