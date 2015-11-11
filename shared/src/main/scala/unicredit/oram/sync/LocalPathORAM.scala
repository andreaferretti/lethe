package unicredit.oram
package sync

import java.util.Random

import transport.Remote
import client._
import storage._


trait AbstractLocalPathORAM[K, V, Id <: K, Doc <: V] extends PathORAM[K, V, Id, Doc] {
  var position = Map.empty[K, Path]

  override def getPosition(id: K) = position.getOrElse(id, Path.random(L))

  override def putPosition(id: K, path: Path) = {
    position += (id -> path)
  }
}

class LocalPathORAM[K, V, Id <: K, Doc <: V](
  val client: StandardClient[(K, V)],
  val stash: Stash[K, V],
  val rng: Random,
  val emptyID: Id,
  val empty: Doc,
  val L: Int,
  val Z: Int
) extends AbstractLocalPathORAM[K, V, Id, Doc]

object LocalPathORAM {
  import boopickle.Default._
  import java.security.SecureRandom

  def apply[K, V, Id <: K, Doc <: V](
    remote: Remote,
    passPhrase: String,
    emptyID: Id,
    empty: Doc,
    L: Int,
    Z: Int
  )(implicit picker: Pickler[(K, V)]) =
    new LocalPathORAM(
      StandardClient[(K, V)](remote, passPhrase),
      MapStash.empty[K, V],
      new SecureRandom,
      emptyID,
      empty,
      L,
      Z)

  def default(remote: Remote, passPhrase: String, L: Int = 8, Z: Int = 4) =
    apply[Int, String, Int, String](remote, passPhrase, -1, "", L, Z)
}