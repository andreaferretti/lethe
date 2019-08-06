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

import oram._
import transport.Remote


class ORAMIndex[Id, Bin](oram: ORAM[Bin, Map[Id, Path]], L: Int, bin: Id => Bin)
  (implicit rng: Random) extends Index[Id] {

  override def getPosition(id: Id) =
    oram.read(bin(id)).getOrElse(id, Path.random(L))
  override def putRandom(id: Id) = {
    val b = bin(id)
    val map  = oram.read(b) + (id -> Path.random(L))
    oram.write(b, map)
  }
  override def init = oram.init

  override def serialize = ???
}

object ORAMIndex {
  import java.security.SecureRandom
  import boopickle.Default._
  import Path.pathPickler


  def local[Id: Pickler, Bin: Pointed: Pickler](
    remote: Remote,
    passPhrase: String,
    params: Params,
    bin: Id => Bin
  ) = {
    implicit val pmap = Pointed(Map.empty[Id, Path])
    implicit val rng = new SecureRandom
    val index = PathORAM[Bin, Map[Id, Path], Bin, Map[Id, Path]](remote, passPhrase, params)

    new ORAMIndex(index, params.depth, bin)
  }
}