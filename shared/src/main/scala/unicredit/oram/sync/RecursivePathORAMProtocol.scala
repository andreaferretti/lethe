package unicredit.oram
package sync

import boopickle.Default._


trait RecursivePathORAMProtocol[Id, Doc, Bin] extends PathORAMProtocol[Id, Doc] { self: BasicClient[Id, Doc] =>
  def bin(id: Id): Bin
  def emptyBin: Bin
  def passPhrase: String
  implicit def pickleId: Pickler[Id]
  implicit def pickleBin: Pickler[Bin]
  import Path.pathPickler

  class InternalORAM extends LocalPathORAMProtocol[Bin, Map[Id, Path]] with BasicClient[Bin, Map[Id, Path]] {
    val Z = self.Z
    val L = self.L
    val emptyID = self.emptyBin
    val passPhrase = self.passPhrase
    val remote = self.remote
    val rng = self.rng
    val empty = Map.empty[Id, Path]
    implicit val pickle = implicitly[Pickler[(Bin, Map[Id, Path])]]

    def decryptBytes(a: Array[Byte]) = self.decryptBytes(a)
    def encryptBytes(a: Array[Byte]) = self.encryptBytes(a)
  }

  val index = new InternalORAM

  override def getPosition(id: Id) =
    index.read(bin(id)).getOrElse(id, Path.random(L))

  override def putPosition(id: Id, path: Path) = {
    val map  = index.read(bin(id)) + (id -> path)
    index.write(bin(id), map)
  }
}