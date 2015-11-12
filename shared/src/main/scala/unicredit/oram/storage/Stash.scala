package unicredit.oram
package storage


trait Stash[Id, Doc] {
  def getOrElse(id: Id, default: Doc): Doc
  def ++=(xs: Seq[(Id, Doc)]): Unit
  def --=(xs: Set[Id]): Unit
  def filter(f: (Id, Doc) => Boolean): Unit
  def take(n: Int)(f: (Id, Doc) => Boolean): Map[Id, Doc]

  def serialize: Array[Byte]
}