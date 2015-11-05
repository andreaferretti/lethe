package unicredit.oram
package search

import java.util.UUID

import sync.ORAM


trait DocumentStore[Doc, Term] {
  def chunker: Chunker[Doc, Term]

  def index: ORAM[Term, Set[UUID]]
  def oram: ORAM[UUID, Doc]

  def addDocument(doc: Doc) = {
    val uuid = UUID.randomUUID
    val terms = chunker.chunks(doc)
    oram.write(uuid, doc)
    for (term <- terms) {
      val docs = index.read(term)
      index.write(term, docs + uuid)
    }
  }

  def search(term: Term): Set[Doc] =
    index.read(term) map oram.read
}