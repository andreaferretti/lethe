package unicredit.lethe
package sync


class WrapORAM[K, V, K1 <: K, V1 <:V, Id, Doc] (
  inner: PathORAM[K, V, K1, V1],
  bijId: Bijection[Id, K1],
  bijDoc: Bijection[Doc, V1]
) extends ORAM[Id, Doc] {
  override def read(id: Id) = bijDoc.to(inner.read(bijId.from(id)))

  override def write(id: Id, doc: Doc) =
    inner.write(bijId.from(id), bijDoc.from(doc))

  override def init = inner.init
}