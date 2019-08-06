/* Copyright 2016 UniCredit S.p.A.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package unicredit.lethe
package search

import java.util.UUID

import oram.ORAM


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