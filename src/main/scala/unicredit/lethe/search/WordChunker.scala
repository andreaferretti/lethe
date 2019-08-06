/* Copyright 2016-2019 UniCredit S.p.A.
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
package unicredit.lethe.search


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