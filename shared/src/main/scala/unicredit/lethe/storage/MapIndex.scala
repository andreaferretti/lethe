package unicredit.lethe
package storage

import java.util.Random

import boopickle.Default._

import serialization._


class MapIndex[Id: Pickler](L: Int)(implicit rng: Random) extends Index[Id] {
  var index = Map.empty[Id, Path]

  override def getPosition(id: Id) =
    index.getOrElse(id, Path.random(L))
  override def putRandom(id: Id) = {
    index += (id -> Path.random(L))
  }
  override def init = ()

  override def serialize = {
    import Path.pathPickler
    new BooSerializer[Map[Id, Path]].encode(index)
  }
}

object MapIndex {
  import Path.pathPickler

  def apply[Id: Pickler](L: Int)(implicit rng: Random) =
    new MapIndex[Id](L)

  def apply[Id: Pickler](a: Array[Byte])(implicit rng: Random) = {
    val s = new BooSerializer[Map[Id, Path]]
    new MapStash(s.decode(a))
  }
}