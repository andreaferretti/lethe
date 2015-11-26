package unicredit.oram
package data

import java.util.UUID

import sync.ORAM


class DataStore[Data, Field](
  index: ORAM[Field, Set[UUID]],
  oram: ORAM[UUID, Data],
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