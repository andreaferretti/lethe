Implementation of https://eprint.iacr.org/2013/280.pdf

TODO:

* Lazy init
* Add search on top of key/value store
* Serialize client sessions
* Async implementation
* Compile SQL queries to sequences of get/search operations
* More robust server

NOTES:
------

A `Remote` is an interface to read and write blocks of data, possibly to a
server. A trivial example is `MemoryRemote`, that just keeps the blocks in
memory without sending them at all. Alternatively, `ZMQRemote` talks to an
actual remote ZeroMQ server.

A `Serializer[A]` is used to convert between and `Array[Byte]` and back;
by default we use BooPickle to handle this.

A `Crypter` handles encryptions and decryption, working at the byte array
level. An example is the `AESCrypter`.

A `Client[A]` has access to all of them, and used this to talk to the server,
sending and receiving encrypted instances of `A`. In particular,
`StandardClient[A]` just puts together the three.

An `ORAM` has access to a `Client` that communicates to a server
and makes sure that one can read or write instances of `Doc`, indexed by `Id`.
The `TrivialORAM` does this by always reading and writing back all
documents for all operations.

`PathORAM` does this by implementing the actual Path ORAM construction.
The index that maps each `Id` to the relative `Path` is kept abstract in
order to handle recursion. In `LocalPathORAM`, we specialize the index
to be a local `Map[Id, Path]`, while `RecursivePathORAM` stores the
index as an ORAM itself.