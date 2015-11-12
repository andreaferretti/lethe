package unicredit.oram
package storage

import java.util.Random

import sync._
import transport.Remote


class ORAMIndex[Id, Bin](oram: ORAM[Bin, Map[Id, Path]], L: Int, bin: Id => Bin)
  (implicit rng: Random) extends Index[Id] {

  override def getPosition(id: Id) =
    oram.read(bin(id)).getOrElse(id, Path.random(L))
  override def putPosition(id: Id, path: Path) = {
    val b = bin(id)
    val map  = oram.read(b) + (id -> path)
    oram.write(b, map)
  }
}

object ORAMIndex {
  import java.security.SecureRandom
  import boopickle.Default._
  import Path.pathPickler


  def local[Id: Pickler, Bin: Pointed: Pickler](
    remote: Remote,
    passPhrase: String,
    L: Int,
    Z: Int,
    bin: Id => Bin,
    offset: Int = 0
  ) = {
    implicit val pmap = Pointed(Map.empty[Id, Path])
    implicit val rng = new SecureRandom
    val index = LocalPathORAM[Bin, Map[Id, Path], Bin, Map[Id, Path]](
      remote, passPhrase, L, Z, offset + pow(2, L))

    new ORAMIndex(index, L, bin)
  }
}