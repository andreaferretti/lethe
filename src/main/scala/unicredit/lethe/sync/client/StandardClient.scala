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
package client

import java.util.Random

import serialization.{ Serializer, BooSerializer }
import crypto.{ Crypter, AESCrypter, AESMaterial }
import transport.Remote


class StandardClient[A](
  serializer: Serializer[A],
  crypter: Crypter,
  remote: Remote
) extends Client[A] {
  override def capacity = remote.capacity

  override def fetchClear(n: Int) =
    serializer.decode(crypter.decrypt(remote.fetch(n)))

  override def putClear(n: Int, data: A) =
    remote.put(n, crypter.encrypt(serializer.encode(data)))

  override def init(data: Seq[A], start: Int) = {
    val bytes = data map { d =>
      crypter.encrypt(serializer.encode(d))
    }
    remote.init(bytes, start)
  }

  def serialize = crypter.serialize
}

object StandardClient {
  import boopickle.Default._

  def apply[A: Pickler](remote: Remote, passPhrase: String)(implicit rng: Random) =
    new StandardClient[A](
      new BooSerializer[A],
      AESCrypter(passPhrase, rng),
      remote
    )

  def apply[A: Pickler](remote: Remote, material: AESMaterial)(implicit rng: Random) =
    new StandardClient[A](
      new BooSerializer[A],
      AESCrypter(material, rng),
      remote
    )
}