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
package unicredit.lethe
package storage


trait Stash[Id, Doc] {
  def getOrElse(id: Id, default: Doc): Doc
  def ++=(xs: Seq[(Id, Doc)]): Unit
  def --=(xs: Set[Id]): Unit
  def filter(f: (Id, Doc) => Boolean): Unit
  def take(n: Int)(f: (Id, Doc) => Boolean): Map[Id, Doc]

  def serialize: Array[Byte]
}