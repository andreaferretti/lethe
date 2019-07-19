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
package sync

import java.util.Random

import boopickle.Default._

import client._
import storage._
import transport.Remote
import serialization._


case class PathORAMMaterial(
  stash: Array[Byte],
  index: Array[Byte],
  params: Params
)

class PathORAM[K, V, Id <: K : Pointed, Doc <: V : Pointed](
  client: StandardClient[(K, V)],
  stash: Stash[K, V],
  index: Index[K],
  params: Params
) extends ORAM[Id, Doc] {
  sealed trait Op
  case object Read extends Op
  case object Write extends Op

  type Bucket = Seq[(K, V)]
  val emptyId = implicitly[Pointed[Id]].empty
  val emptyDoc = implicitly[Pointed[Doc]].empty
  val offset = params.offset
  val L = params.depth
  val Z = params.bucketSize

  override def read(id: Id) = access(Read, id, emptyDoc)
  override def write(id: Id, doc: Doc) = access(Write, id, doc)

  def fetchBucket(p: Path, ℓ: Int): Bucket = {
    val start = (p.take(ℓ).int - 1 + offset) * Z
    (0 until Z) map { i => client.fetchClear(start + i) }
  }

  def putBucket(p: Path, ℓ: Int, bucket: Bucket): Unit = {
    val start = (p.take(ℓ).int - 1 + offset) * Z
    for (i <- 0 until Z) {
      val item = if (i < bucket.length) bucket(i) else (emptyId, emptyDoc)
      client.putClear(start + i, item)
    }
  }

  def access(op: Op, id: Id, doc: Doc) = {
    val x = index.getPosition(id)
    index.putRandom(id)
    for (ℓ <- 0 to L) {
      stash ++= fetchBucket(x, ℓ)
    }
    val data = stash.getOrElse(id, emptyDoc)
    if (op == Write) {
      stash ++= List(id -> doc)
    }
    stash filter { case (a, _) => a != emptyId }
    for (ℓ <- (0 to L).reverse) {
      val stash_ = stash.take(Z){ case (a, _) =>
        x.take(ℓ) == index.getPosition(a).take(ℓ)
      }
      stash --= stash_.keySet
      putBucket(x, ℓ, stash_.toSeq)
    }
    data.asInstanceOf[Doc]
  }

  def init: Unit = {
    val items = (0 until params.numSlots) map { _ => (emptyId, emptyDoc) }

    client.init(items, offset * Z)
    index.init
  }

  def serialize: Array[Byte] = {
    val material = PathORAMMaterial(stash.serialize, index.serialize, params)
    new BooSerializer[PathORAMMaterial].encode(material)
  }
}

object PathORAM {
  import boopickle.Default._
  import java.security.SecureRandom

  def apply[K: Pickler, V: Pickler, Id <: K : Pointed, Doc <: V : Pointed](
    remote: Remote,
    passPhrase: String,
    params: Params
  ) = {
    implicit val rng = new SecureRandom

    new PathORAM(StandardClient[(K, V)](remote, passPhrase),
      MapStash.empty[K, V], MapIndex[K](params.depth), params)
  }

  def recursive[Id: Pointed: Pickler, Doc: Pointed: Pickler, Bin: Pointed: Pickler](
    remote: Remote,
    passPhrase: String,
    params: Params,
    bin: Id => Bin
  ) = {
    implicit val rng = new SecureRandom
    val indexParams = params.withNextOffset
    val index = ORAMIndex.local[Id, Bin](remote, passPhrase, indexParams, bin)
    val client = StandardClient[(Id, Doc)](remote, passPhrase)
    val stash = MapStash.empty[Id, Doc]

    new PathORAM[Id, Doc, Id, Doc](client, stash, index, params)
  }

  def apply[K: Pickler, V: Pickler, Id <: K : Pointed, Doc <: V : Pointed](
    client: StandardClient[(K, V)],
    a: Array[Byte]
  ) = {
    implicit val rng = new SecureRandom
    val material = new BooSerializer[PathORAMMaterial].decode(a)
    val stash = MapStash[K, V](material.stash)
    val index = MapIndex[K](material.params.depth, material.index)

    new PathORAM(client, stash, index, material.params)
  }
}