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

class LocalPathORAM[K, V, Id <: K : Pointed, Doc <: V : Pointed](
  val client: StandardClient[(K, V)],
  val stash: Stash[K, V],
  val rng: Random,
  val L: Int,
  val Z: Int
) extends AbstractLocalPathORAM[K, V, Id, Doc] {
  val empty = implicitly[Pointed[Doc]].empty
  val emptyID = implicitly[Pointed[Id]].empty
}

object LocalPathORAM {
  import boopickle.Default._
  import java.security.SecureRandom

  implicit val pint = Pointed(-1)
  implicit val pstring = Pointed("")

  def apply[K: Pickler, V: Pickler, Id <: K : Pointed, Doc <: V : Pointed](
    remote: Remote,
    passPhrase: String,
    L: Int,
    Z: Int
  ) = new LocalPathORAM(
    StandardClient[(K, V)](remote, passPhrase),
    MapStash.empty[K, V],
    new SecureRandom,
    L,
    Z)

  def default(remote: Remote, passPhrase: String, L: Int = 8, Z: Int = 4) =
    apply[Int, String, Int, String](remote, passPhrase, L, Z)
}