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

import java.util.Random

import boopickle.Default._

import serialization._


class MapIndex[Id: Pickler](L: Int, var index: Map[Id, Path] = Map.empty[Id, Path])(implicit rng: Random) extends Index[Id] {
  override def getPosition(id: Id) =
    index.getOrElse(id, Path.random(L))
  override def putRandom(id: Id) = {
    index += (id -> Path.random(L))
  }
  override def init = ()

  override def serialize = {
    import Path.pathPickler
    new BooSerializer[Map[Id, Path]].encode(index)
  }
}

object MapIndex {
  import Path.pathPickler

  def apply[Id: Pickler](L: Int)(implicit rng: Random) =
    new MapIndex[Id](L)

  def apply[Id: Pickler](L: Int, a: Array[Byte])(implicit rng: Random) = {
    val s = new BooSerializer[Map[Id, Path]]
    new MapIndex(L, s.decode(a))
  }
}