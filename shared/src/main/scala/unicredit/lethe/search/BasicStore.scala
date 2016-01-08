package unicredit.lethe
package search

import java.util.UUID

import sync.ORAM


class BasicStore(
  val index: ORAM[String, Set[UUID]],
  val oram: ORAM[UUID, String]
) extends DocumentStore[String, String] {
  val chunker = new WordChunker
}