package unicredit.lethe
package storage

import boopickle.Default._

import serialization.BooSerializer


class MapStash[Id: Pickler, Doc: Pickler](s: Map[Id, Doc]) extends Stash[Id, Doc] {
  var stash = s

  override def getOrElse(id: Id, default: Doc) = stash.getOrElse(id, default)
  override def ++=(xs: Seq[(Id, Doc)]) = stash ++= xs
  override def --=(xs: Set[Id]) = stash --= xs
  override def filter(f: (Id, Doc) => Boolean) = {
    stash = stash filter f.tupled
  }
  override def take(n: Int)(f: (Id, Doc) => Boolean) =
    stash filter f.tupled take n

  override def serialize = {
    val s = new BooSerializer[Map[Id, Doc]]
    s.encode(stash)
  }
}

object MapStash {
  def empty[Id: Pickler, Doc: Pickler] = new MapStash[Id, Doc](Map())

  def apply[Id: Pickler, Doc: Pickler](a: Array[Byte]) = {
    val s = new BooSerializer[Map[Id, Doc]]
    new MapStash(s.decode(a))
  }
}