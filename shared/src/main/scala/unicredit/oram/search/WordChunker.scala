package unicredit.oram.search


class WordChunker extends Chunker[String, String] {
  private val allSpaces = """^[\s]*$""".r
  private val digits = """^[\d\.]*$""".r
  private def isEmpty(sentence: String) =
    allSpaces.findFirstMatchIn(sentence).isEmpty

  private def isNumeric(text: String) =
    digits.findFirstMatchIn(text).isDefined

  private def sentences(text: String) =
    (text split """[\t\n\.!\?…]""" filter isEmpty).toList

  private def tokens(text: String): List[List[String]] = tokens(sentences(text))
  private def tokens(sentences: List[String]): List[List[String]] =
    sentences map sentenceToTokens

  private def sentenceToTokens(sentence: String): List[String] =
    (sentence split """[\s\(\)\,;:“”'@#"]""").toList
      .filter(_.length > 2)
      .filterNot(isNumeric)

  private def cleanTokens(text: String, stopWords: List[String]) =
    tokens(text) map { s => s.filter {t =>
      (t.length > 2) && ! (stopWords contains t)
    }}
  override def chunks(doc: String) = tokens(doc).flatten
}