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

import java.util.Random
import scala.collection.mutable.{ BitSet => MBitSet }
import scala.collection.BitSet

import boopickle.Default._


class Path(val int: Int) {
  val bits: BitSet = MBitSet.fromBitMaskNoCopy(Array(int.toLong))
  def apply(i: Int) = bits(i)
  def level = bits.max
  def take(k: Int) = new Path(int >> ((level - k) max 0))

  override def equals(other: Any) = other match {
    case p: Path => p.int == int
    case _ => false
  }

  override def toString = (0 to level).reverse.
    map({ b => if (bits(b)) '1' else '0' }).
    mkString("Path[",  ",", "]")
}

object Path {
  def apply(n: Int) = new Path(n)
  def random(L: Int)(implicit rng: Random) = {
    val cap = pow(2, L)
    new Path(rng.nextInt(cap) + cap)
  }

  implicit val pathPickler = transformPickler[Path, Int](apply)(_.int)
}