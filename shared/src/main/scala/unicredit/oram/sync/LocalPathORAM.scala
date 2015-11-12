package unicredit.oram
package sync

import java.util.Random

import transport.Remote
import client._
import storage._


trait AbstractLocalPathORAM[K, V, Id <: K, Doc <: V] extends PathORAM[K, V, Id, Doc] {
  def index: Index[K]

  override def getPosition(id: K) = index.getPosition(id)

  override def putPosition(id: K, path: Path) = index.putPosition(id, path)
}

class LocalPathORAM[K, V, Id <: K : Pointed, Doc <: V : Pointed](
  val client: StandardClient[(K, V)],
  val stash: Stash[K, V],
  val index: Index[K],
  val rng: Random,
  val L: Int,
  val Z: Int,
  val offset: Int = 0
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
    Z: Int,
    offset: Int = 0
  ) = {
    val rng = new SecureRandom

    new LocalPathORAM(StandardClient[(K, V)](remote, passPhrase),
      MapStash.empty[K, V], MapIndex[K](L)(rng), rng, L, Z)
  }

  def default(remote: Remote, passPhrase: String, L: Int = 8, Z: Int = 4) =
    apply[Int, String, Int, String](remote, passPhrase, L, Z)
}