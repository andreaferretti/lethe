Implementation of https://eprint.iacr.org/2013/280.pdf

TODO:

* Lazy init
* Add search on top of key/value store
* Serialize client sessions
* Async implementation
* Alternative server backends (Redis, LevelDB...)
* Compile SQL queries to sequences of get/search operations
* More robust server

NOTES:
------

A `Remote` is an interface to read and write blocks of data, possibly to a
server. A trivial example is `MemoryRemote`, that just keeps the blocks in
memory without sending them at all. Alternatively, `ZMQRemote` talks to an
actual remote ZeroMQ server.

A `Client` has access to a `Remote` and is able to send pairs of an `Id` and
a `Doc` to it. In order to do this, it needs to be able to serialize those
pairs and encrypt them. The trait `BasicClient` uses BooPickle to handle
serialization in a generic manner and can be extended to just add the
encryption and decryption functionality. On top of it, there are an example
`UnencryptedClient` (that encrypts as the identity) and an actual `AESClient`.

An `ORAMProtocol` is a particular `Client` that communicates to a server
and makes sure that one can read or write instances of `Doc`, indexed by `Id`.
The `TrivialORAMProtocol` does this by always reading and writing back all
documents for all operations.

`PathORAMProtocol` does this by implementing the actual Path ORAM construction.
The index that maps each `Id` to the relative `Path` is kept abstract in
order to handle recursion. In `LocalPathORAMProtocol`, we specialize the index
to be a local `Map[Id, Path]`, while `RecursivePathORAMProtocol` stores the
index as an ORAM itself.

To get an actual `ORAM` one has to provide a concrete `ORAMProtocol`, with all
parameters specified, and a concrete `Client`, attached to a particular
`Remote`. Examples of this are `UnsafeORAM` - that uses all the trivial
constructions - and the actual `RecursivePathORAM`.