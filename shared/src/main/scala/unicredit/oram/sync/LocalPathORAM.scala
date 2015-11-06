package unicredit.oram
package sync

import java.util.Random

import transport.Remote
import client._


trait AbstractLocalPathORAM[Id, Doc] extends PathORAM[Id, Doc] {
  var position = Map.empty[Id, Path]

  override def getPosition(id: Id) = position.getOrElse(id, Path.random(L))

  override def putPosition(id: Id, path: Path) = {
    position += (id -> path)
  }
}

class LocalPathORAM[Id, Doc](
  val client: StandardClient[(Id, Doc)],
  val rng: Random,
  val emptyID: Id,
  val empty: Doc,
  val L: Int,
  val Z: Int
) extends AbstractLocalPathORAM[Id, Doc]

object LocalPathORAM {
  import boopickle.Default._
  import java.security.SecureRandom

  def apply[Id, Doc](remote: Remote, passPhrase: String, emptyID: Id, empty: Doc, L: Int, Z: Int)(implicit picker: Pickler[(Id, Doc)]) =
    new LocalPathORAM(StandardClient[(Id, Doc)](remote, passPhrase), new SecureRandom, emptyID, empty, L, Z)

    def default(remote: Remote, passPhrase: String) = apply[Int, String](
      remote, passPhrase, -1, "", 8, 4
    )
}