package unicredit.lethe
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

  def addDocuments(docs: Seq[Doc]) = {
    val indexedDocs = docs map { d => (d, UUID.randomUUID) }
    val docTermPairs = for {
      (doc, uuid) <- indexedDocs
      term <- chunker.chunks(doc)
    } yield (term, uuid)
    val docMap = docTermPairs groupBy (_._1) mapValues { pairs =>
      pairs map (_._2)
    }
    for ((doc, uuid) <- indexedDocs) {
      oram.write(uuid, doc)
    }
    for (term <- docMap.keys) {
      val termDocs = index.read(term)
      index.write(term, termDocs ++ docMap(term))
    }
  }

  def search(term: Term): Set[Doc] =
    index.read(term) map oram.read
}