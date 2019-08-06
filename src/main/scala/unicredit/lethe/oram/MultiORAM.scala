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
package oram

import java.util.Random

import boopickle.Default._

import transport.Remote
import client._
import storage._

class MultiORAM2[
  Id1: Pointed: Pickler,
  Doc1: Pointed: Pickler,
  Id2: Pointed: Pickler,
  Doc2: Pointed: Pickler
] {
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

  def gen(remote: Remote, passPhrase: String, params: Params)(implicit rng: Random) = {
    val stash = MapStash.empty[K, V]
    val index = MapIndex[K](params.depth)
    val client = StandardClient[(K, V)](remote, passPhrase)
    val o1 = new PathORAM[K, V, K1, V1](client, stash, index, params)
    val o2 = new PathORAM[K, V, K2, V2](client, stash, index, params)

    (
      new WrapORAM[K, V, K1, V1, Id1, Doc1](o1, el1[Id1, Id2], el1[Doc1, Doc2]),
      new WrapORAM[K, V, K2, V2, Id2, Doc2](o2, el2[Id1, Id2], el2[Doc1, Doc2])
    )
  }
}

class MultiORAM3[
  Id1: Pointed: Pickler,
  Doc1: Pointed: Pickler,
  Id2: Pointed: Pickler,
  Doc2: Pointed: Pickler,
  Id3: Pointed: Pickler,
  Doc3: Pointed: Pickler
] {
  import Wrap3._

  type K = Wrap3[Id1, Id2, Id3]
  type V = Wrap3[Doc1, Doc2, Doc3]
  type K1 = El1[Id1, Id2, Id3]
  type V1 = El1[Doc1, Doc2, Doc3]
  type K2 = El2[Id1, Id2, Id3]
  type V2 = El2[Doc1, Doc2, Doc3]
  type K3 = El3[Id1, Id2, Id3]
  type V3 = El3[Doc1, Doc2, Doc3]
  implicit val p1 = implicitly[Pointed[Id1]].map(El1[Id1, Id2, Id3](_))
  implicit val p2 = implicitly[Pointed[Id2]].map(El2[Id1, Id2, Id3](_))
  implicit val p3 = implicitly[Pointed[Id3]].map(El3[Id1, Id2, Id3](_))
  implicit val p4 = implicitly[Pointed[Doc1]].map(El1[Doc1, Doc2, Doc3](_))
  implicit val p5 = implicitly[Pointed[Doc2]].map(El2[Doc1, Doc2, Doc3](_))
  implicit val p6 = implicitly[Pointed[Doc3]].map(El3[Doc1, Doc2, Doc3](_))

  def gen(remote: Remote, passPhrase: String, params: Params)(implicit rng: Random) = {
    val stash = MapStash.empty[K, V]
    val index = MapIndex[K](params.depth)
    val client = StandardClient[(K, V)](remote, passPhrase)
    val o1 = new PathORAM[K, V, K1, V1](client, stash, index, params)
    val o2 = new PathORAM[K, V, K2, V2](client, stash, index, params)
    val o3 = new PathORAM[K, V, K3, V3](client, stash, index, params)

    (
      new WrapORAM[K, V, K1, V1, Id1, Doc1](o1, el1[Id1, Id2, Id3], el1[Doc1, Doc2, Doc3]),
      new WrapORAM[K, V, K2, V2, Id2, Doc2](o2, el2[Id1, Id2, Id3], el2[Doc1, Doc2, Doc3]),
      new WrapORAM[K, V, K3, V3, Id3, Doc3](o3, el3[Id1, Id2, Id3], el3[Doc1, Doc2, Doc3])
    )
  }
}