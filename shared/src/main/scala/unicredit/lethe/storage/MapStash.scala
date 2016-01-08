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
package storage

import boopickle.Default._

import serialization.BooSerializer


class MapStash[Id: Pickler, Doc: Pickler](s: Map[Id, Doc]) extends Stash[Id, Doc] {
  var stash = s

  override def getOrElse(id: Id, default: Doc) = stash.getOrElse(id, default)
  override def ++=(xs: Seq[(Id, Doc)]) = stash ++= xs
  override def --=(xs: Set[Id]) = stash --= xs
  override def filter(f: (Id, Doc) => Boolean) = {
    stash = stash filter f.tupled
  }
  override def take(n: Int)(f: (Id, Doc) => Boolean) =
    stash filter f.tupled take n

  override def serialize = {
    val s = new BooSerializer[Map[Id, Doc]]
    s.encode(stash)
  }
}

object MapStash {
  def empty[Id: Pickler, Doc: Pickler] = new MapStash[Id, Doc](Map())

  def apply[Id: Pickler, Doc: Pickler](a: Array[Byte]) = {
    val s = new BooSerializer[Map[Id, Doc]]
    new MapStash(s.decode(a))
  }
}