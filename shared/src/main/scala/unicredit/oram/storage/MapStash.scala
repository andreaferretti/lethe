package unicredit.oram
package storage


class MapStash[Id, Doc] extends Stash[Id, Doc] {
  var stash = Map.empty[Id, Doc]

  override def getOrElse(id: Id, default: Doc) = stash.getOrElse(id, default)
  override def ++=(xs: Seq[(Id, Doc)]) = stash ++= xs
  override def --=(xs: Set[Id]) = stash --= xs
  override def filter(f: (Id, Doc) => Boolean) = {
    stash = stash filter f.tupled
  }
  override def take(n: Int)(f: (Id, Doc) => Boolean) =
    stash filter f.tupled take n
}

object MapStash {
  def empty[Id, Doc] = new MapStash[Id, Doc]
}