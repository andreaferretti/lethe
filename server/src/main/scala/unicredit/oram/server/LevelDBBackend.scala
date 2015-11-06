package unicredit.oram
package server

import java.io.File
import scala.collection.JavaConversions._

import org.iq80.leveldb._
import org.iq80.leveldb.impl.Iq80DBFactory._


class LevelDBBackend(file: File) extends Backend {
  private val options = new Options()
  options.createIfMissing(true)
  private val db = factory.open(file, options)
  var capacityCache = 0

  override def capacity = {
    if (capacityCache == 0) {
      capacityCache = db.iterator.size
    }
    capacityCache
  }
  override def fetch(n: Int) = db.get(Bytes(n))
  override def put(n: Int, a: Array[Byte]) = { db.put(Bytes(n), a) }
  override def init(d: Seq[Array[Byte]]) = {
    val batch = db.createWriteBatch
    for ((a, n) <- d.zipWithIndex) {
      db.put(Bytes(n), a)
    }
    db.write(batch)
    capacityCache = d.length
  }
}