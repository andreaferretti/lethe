package unicredit.lethe
package data

import java.util.UUID

import sync.ORAM


class DataStore[Data, Field](
  oram: ORAM[UUID, Data],
  index: ORAM[Field, Set[UUID]],
  field: Data => Field
) {

  def add(data: Data) = {
    val uuid = UUID.randomUUID
    val f = field(data)
    oram.write(uuid, data)
    val docs = index.read(f)
    index.write(f, docs + uuid)
  }

  def search(f: Field): Set[Data] =
    index.read(f) map oram.read
}

class DataStore2[Data, Field1, Field2](
  oram: ORAM[UUID, Data],
  index1: ORAM[Field1, Set[UUID]],
  index2: ORAM[Field2, Set[UUID]],
  field1: Data => Field1,
  field2: Data => Field2
) {

  def add(data: Data) = {
    val uuid = UUID.randomUUID
    val f1 = field1(data)
    val f2 = field2(data)
    oram.write(uuid, data)
    val docs1 = index1.read(f1)
    index1.write(f1, docs1 + uuid)
    val docs2 = index2.read(f2)
    index2.write(f2, docs2 + uuid)
  }

  def search1(f: Field1): Set[Data] =
    index1.read(f) map oram.read

  def search2(f: Field2): Set[Data] =
    index2.read(f) map oram.read
}

object DataStore {
  import boopickle.Default._
  import sync.transport.Remote
  import sync.MultiORAM

  implicit val uuidPickler = transformPickler[UUID, String](_.toString, UUID.fromString)
  implicit val puuid = Pointed(UUID.fromString("16b01bbe-484b-49e8-85c5-f424a983205f"))
  implicit val puuidset = Pointed(Set.empty[UUID])

  def apply[Data: Pickler: Pointed, Field: Pickler: Pointed](
    f: Data => Field,
    remote: Remote,
    passPhrase: String,
    params: Params
  ) = {
    val (index, oram) = MultiORAM.gen2[Field, Set[UUID], UUID, Data](
      remote, passPhrase, params)

    oram.init
    index.init

    new DataStore(oram, index, f)
  }

  def apply[Data: Pickler: Pointed, Field1: Pickler: Pointed, Field2: Pickler: Pointed](
    f1: Data => Field1,
    f2: Data => Field2,
    remote: Remote,
    passPhrase: String,
    params: Params
  ) = {
    val (index1, index2, oram) = MultiORAM.gen3[
      Field1,
      Set[UUID],
      Field2,
      Set[UUID],
      UUID,
      Data
    ](remote, passPhrase, params)

    oram.init
    index1.init
    index2.init

    new DataStore2(oram, index1, index2, f1, f2)
  }
}