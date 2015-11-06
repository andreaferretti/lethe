package unicredit.oram
package sync

import boopickle.Default._

import serialization.BooSerializer


trait RecursivePathORAMProtocol[Id, Doc, Bin] extends PathORAMProtocol[Id, Doc] { self =>
  def bin(id: Id): Bin
  def emptyBin: Bin
  implicit def pickleId: Pickler[Id]
  implicit def pickleBin: Pickler[Bin]
  import Path.pathPickler

  class InternalORAM extends LocalPathORAMProtocol[Bin, Map[Id, Path]] {
    val Z = self.Z
    val L = self.L
    val emptyID = self.emptyBin
    implicit val pickle = implicitly[Pickler[(Bin, Map[Id, Path])]]
    val client = self.client.withSerializer(new BooSerializer[(Bin, Map[Id, Path])])
    lazy val rng = self.rng
    val empty = Map.empty[Id, Path]
  }

  val index = new InternalORAM

  override def getPosition(id: Id) =
    index.read(bin(id)).getOrElse(id, Path.random(L))

  override def putPosition(id: Id, path: Path) = {
    val map  = index.read(bin(id)) + (id -> path)
    index.write(bin(id), map)
  }
}