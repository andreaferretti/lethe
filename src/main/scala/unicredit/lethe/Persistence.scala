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

import scala.collection.JavaConversions._
import java.util.Random

import better.files._, Dsl._
import boopickle.Default._

import oram.PathORAM
import transport.Remote

object Persistence {
  def save[K, V, Id <: K : Pointed, Doc <: V : Pointed](
    path: String,
    oram: PathORAM[K, V, Id, Doc]
  ) = File(path).writeBytes(oram.serialize.toIterator)

  def restorePathORAM[K: Pickler, V: Pickler, Id <: K : Pointed, Doc <: V : Pointed](
    remote: Remote,
    path: String
  )(implicit rng: Random) = PathORAM[K, V, Id, Doc](remote, File(path).loadBytes)
}