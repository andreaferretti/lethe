package unicredit.lethe.search


trait Chunker[Doc, Term] {
  def chunks(doc: Doc): Seq[Term]

  def chunks(docs: Seq[Doc]): Seq[Term] = docs flatMap chunks
}