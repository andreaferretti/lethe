// package unicredit.oram
// package sync
//
// import java.util.Random
//
// import boopickle.Default._
//
// import serialization.BooSerializer
// import transport.Remote
// import client._
// import storage._
//
//
// trait AbstractRecursivePathORAM[Id, Doc, Bin] extends PathORAM[
//     Either[Bin, Id],
//     Either[Map[Id, Path], Doc],
//     Right[Bin, Id],
//     Right[Map[Id, Path], Doc]
//   ] {
//   def bin(id: Id): Bin
//
//   val stash = MapStash.empty[Either[Bin, Id], Either[Map[Id, Path], Doc]]
//   implicit val pbin = Pointed(bin(emptyID.right.get))
//   implicit val pmap = Pointed(Map.empty[Id, Path])
//
//   val index = MultiORAM.left[Bin, Map[Id, Path], Id, Doc](
//     client = client,
//     stash = stash,
//     rng = rng,
//     L = L,
//     Z = Z
//   )
//
//   override def getPosition(id: Id) =
//     index.read(bin(id)).getOrElse(id, Path.random(L))
//
//   override def putPosition(id: Id, path: Path) = {
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