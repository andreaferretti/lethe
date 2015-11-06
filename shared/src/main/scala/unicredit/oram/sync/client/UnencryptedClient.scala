package unicredit.oram
package sync
package client

import boopickle.Default._

import crypto.NoCrypter
import serialization.BooSerializer
import transport.Remote


class UnencryptedClient[A](remote: Remote)(implicit pickler: Pickler[A])
  extends StandardClient[A](new BooSerializer[A], new NoCrypter, remote)