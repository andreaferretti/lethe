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
package server

import java.io.File
import scala.collection.JavaConverters._

import org.iq80.leveldb._
import org.iq80.leveldb.impl.Iq80DBFactory._

class LevelDBBackend(file: File) extends Backend {
  private val options = new Options()
  options.createIfMissing(true)
  private val db = factory.open(file, options)
  var capacityCache = 0

  override def capacity = {
    if (capacityCache == 0) {
      capacityCache = db.iterator.asScala.size
    }
    capacityCache
  }
  override def fetch(n: Int) = db.get(Bytes(n))
  override def put(n: Int, a: Array[Byte]) = { db.put(Bytes(n), a) }
  override def init(d: Seq[Array[Byte]], start: Int) = {
    val batch = db.createWriteBatch
    for ((a, n) <- d.zipWithIndex) {
      db.put(Bytes(n + start), a)
    }
    db.write(batch)
    capacityCache = 0
  }
}
