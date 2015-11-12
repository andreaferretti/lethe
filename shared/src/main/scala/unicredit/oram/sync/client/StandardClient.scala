package unicredit.oram
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