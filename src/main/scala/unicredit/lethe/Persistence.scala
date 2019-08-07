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

import scala.collection.JavaConversions._
import java.util.Random

import better.files._, Dsl._
import boopickle.Default._

import oram._
import transport.Remote

object Persistence {
  def save[K, V, Id <: K : Pointed, Doc <: V : Pointed](
    path: String,
    oram: PathORAM[K, V, Id, Doc]
  ) = File(path).writeBytes(oram.serialize.toIterator)

  def save[K, V, K1 <: K, V1 <:V, Id, Doc](
    path: String,
    oram: WrapORAM[K, V, K1, V1, Id, Doc]
  ) = File(path).writeBytes(oram.serialize.toIterator)

  def restorePathORAM[K: Pickler, V: Pickler, Id <: K : Pointed, Doc <: V : Pointed](
    remote: Remote,
    path: String
  )(implicit rng: Random) = PathORAM[K, V, Id, Doc](remote, File(path).loadBytes)

  def restoreMultiORAM2[
    Id1: Pointed: Pickler,
    Doc1: Pointed: Pickler,
    Id2: Pointed: Pickler,
    Doc2: Pointed: Pickler
  ](
    remote: Remote,
    path1: String,
    path2: String
  )(implicit rng: Random) = {
    import Wrap2._

    type K = Wrap2[Id1, Id2]
    type V = Wrap2[Doc1, Doc2]
    type K1 = El1[Id1, Id2]
    type V1 = El1[Doc1, Doc2]
    type K2 = El2[Id1, Id2]
    type V2 = El2[Doc1, Doc2]
    implicit val p1 = implicitly[Pointed[Id1]].map(El1[Id1, Id2](_))
    implicit val p2 = implicitly[Pointed[Id2]].map(El2[Id1, Id2](_))
    implicit val p3 = implicitly[Pointed[Doc1]].map(El1[Doc1, Doc2](_))
    implicit val p4 = implicitly[Pointed[Doc2]].map(El2[Doc1, Doc2](_))

    val o1 = restorePathORAM[K, V, K1, V1](remote, path1)
    val o2 = restorePathORAM[K, V, K2, V2](remote, path2)
    val multiOramGen = new MultiORAM2[Id1, Doc1, Id2, Doc2]
    multiOramGen.restore(o1, o2)
  }
}