package unicredit.oram
package storage

import java.util.Random


class MapIndex[Id](L: Int)(implicit rng: Random) extends Index[Id] {
  var index = Map.empty[Id, Path]

  override def getPosition(id: Id) =
    index.getOrElse(id, Path.random(L))
  override def putPosition(id: Id, path: Path) = {
    index += (id -> path)
  }
  override def init = ()
}

object MapIndex {
  def apply[Id](L: Int)(implicit rng: Random) =
    new MapIndex[Id](L)(rng)
}