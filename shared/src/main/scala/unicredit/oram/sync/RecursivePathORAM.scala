package unicredit.oram
package sync
//
// import java.util.Random
//
// import boopickle.Default._
//
// import serialization.BooSerializer
import transport.Remote
import client._
import storage._
//
//
// trait AbstractRecursivePathORAM[Id, Doc, Bin] extends PathORAM[Id, Doc, Id, Doc] {
//   def bin(id: Id): Bin
//
//   val stash = MapStash.empty[Either[Bin, Id], Either[Map[Id, Path], Doc]]
//   implicit val pbin = Pointed(bin(emptyID.right.get))
//   implicit val pmap = Pointed(Map.empty[Id, Path])
//
//   def index: ORAM[Id, Path]
//   val offset = pow(2, L)
//
//   override def getPosition(id: Either[Bin, Id]) =
//     index.read(bin(id)).getOrElse(id, Path.random(L))
//
//   override def putPosition(id: Either[Bin, Id], path: Path) = {
//     val map  = index.read(bin(id)) + (id -> path)
//     index.write(bin(id), map)
//   }
// }
//
// class RecursivePathORAM[Id: Pointed, Doc: Pointed, Bin](
//   val client: StandardClient[(Either[Bin, Id], Either[Map[Id, Path], Doc])],
//   val rng: Random,
//   val L: Int,
//   val Z: Int,
//   val binf: Id => Bin
// )(implicit val pickleId: Pickler[Id], val pickleBin: Pickler[Bin]) extends AbstractRecursivePathORAM[Id, Doc, Bin] {
//   def bin(id: Id) = binf(id)
//   val empty = Right[Map[Id, Path], Doc](implicitly[Pointed[Doc]].empty)
//   val emptyID = Right[Bin, Id](implicitly[Pointed[Id]].empty)
// }
//
// object RecursivePathORAM {
//   import boopickle.Default._
//   import java.security.SecureRandom
//   import Path.pathPickler
//
//   implicit val pint = Pointed(-1)
//   implicit val pstring = Pointed("")
//
//   def apply[Id: Pointed: Pickler, Doc: Pointed: Pickler, Bin: Pickler](
//     remote: Remote,
//     passPhrase: String,
//     L: Int,
//     Z: Int,
//     bin: Id => Bin
//   ) = new RecursivePathORAM[Id, Doc, Bin](
//     StandardClient[(Either[Bin, Id], Either[Map[Id, Path], Doc])](remote, passPhrase),
//     new SecureRandom, L, Z, bin
//   )
//
//   def default(remote: Remote, passPhrase: String, L: Int = 8, Z: Int = 4) =
//     apply[Int, String, Int](remote, passPhrase, L, Z, _ % 1024)
// }

object RecursivePathORAM {
  import boopickle.Default._
  import java.security.SecureRandom

  def apply[Id: Pointed: Pickler, Doc: Pointed: Pickler, Bin: Pointed: Pickler](
    remote: Remote,
    passPhrase: String,
    L: Int,
    Z: Int,
    offset: Int,
    bin: Id => Bin
  ) = {
    val rng = new SecureRandom
    val index = ORAMIndex.local[Id, Bin](
      remote, passPhrase, L, Z, offset + pow(2, L + 1) - 1, bin)
    val client = StandardClient[(Id, Doc)](remote, passPhrase)
    val stash = MapStash.empty[Id, Doc]

    new LocalPathORAM[Id, Doc, Id, Doc](client, stash, index, rng, L, Z, offset)
  }
}