package unicredit.oram
package sync
package client

import java.nio.ByteBuffer

import serialization.{ Serializer, BooSerializer }
import crypto.{ Crypter, AESCrypter }
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

  override def init(data: Seq[A]) = {
    val bytes = data map { d =>
      crypter.encrypt(serializer.encode(d))
    }
    remote.init(bytes)
  }

  def withSerializer[B](s: Serializer[B]) =
    new StandardClient(s, crypter, remote)
}

object StandardClient {
  import boopickle.Default._

  def apply[A](remote: Remote, passPhrase: String)(implicit pickler: Pickler[A]) =
    new StandardClient[A](
      new BooSerializer[A],
      AESCrypter(passPhrase),
      remote
    )
}