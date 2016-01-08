#Lethe

![logo](https://raw.githubusercontent.com/unicredit/lethe/master/lethe.png)

This is a library designed to provide an [Oblivious RAM](http://outsourcedbits.org/2013/12/20/how-to-search-on-encrypted-data-part-4-oblivious-rams/)
and some higher-level tools on top of it.

More specifically, Lethe is an implementation of [Path ORAM](https://eprint.iacr.org/2013/280.pdf).

Table of contents
-----------------

<!-- TOC depthFrom:1 depthTo:6 withLinks:1 updateOnSave:1 orderedList:0 -->

- [Lethe](#lethe)
	- [So, what is an oblivious RAM?](#so-what-is-an-oblivious-ram)
	- [Goals](#goals)
	- [Status](#status)
	- [Glossary](#glossary)

<!-- /TOC -->

##So, what is an oblivious RAM?

Is is a model that helps when designing applications that delegate an untrusted
server with access to their data (think cloud computing). Of course, in such a
situation, one should encrypt the data on the client side.

[Turns out](https://www.internetsociety.org/sites/default/files/06_1.pdf) this
is [not enough](https://eprint.iacr.org/2013/163.pdf). Observation of the access
patterns to the data can allow an attacker to gain significant information. As
shown in the papers above, actual attacks can be mounted and this is not only a
theoretical concern.

Oblivious RAM models a random access data structure giving guarantees that the
attacker will not learn anything other than the frequency of access and the
overall size of data. Various techniques exist, all based on the idea of
continuously shuffling memory as it is being accessed.

Lethe is an implementation of [Path ORAM](https://eprint.iacr.org/2013/280.pdf).

##Goals

The end goal is to provide a complete implementation of Path ORAM in Scala, that
works both on the JVM and in [Scala.js](http://www.scala-js.org/).

On top of that, various higher-level features can be constructed:

* data indices to allow serching encrypted data
* support for SQL-like queries
* [oblivious data structures](https://eprint.iacr.org/2014/185.pdf)

Eventually, the long term aim would be to provide a deployable server together
with a client library of data structures that can be mapped on the server and
some form of SQL support.

##Status

Currently Lethe contains a basic implementation of Path ORAM that uses
[ZeroMQ](http://zeromq.org/) to communicate synchronously with a server.
The server is very minimal, and can either work in memory, or use
[LevelDB](https://github.com/wlu-mstr/leveldb-java) as a storage backend to
persist data across sessions.

A recursive form of the Path ORAM algorithm is used to minimize the amount of
data that the client has to keep.

On top of that, a very basic form of indexing is developed, allowing search
on both structured data and free text.

A lot remains to be done, and here is a tentative plan:

* Clients have to mantain some form of key to get access to the data. This
  includes both a private key in the traditional sense, and additional
  information that allows to reconstruct to structure of data on the server.
  Everything is designed so that such information is serializable, but a
  mechanism to save the key to a file and restore a previous session is not
  implemented yet.
* Making it work in Scala.js. Most of the work is done: we have chosen
  libraries that cross-compile, and the communication with the server itself
  is handled by a very simple interface, allowing other transports than
  ZeroMQ. But in the browser we cannot use a synchronous model of communication,
  and this impacts the algorithms. Once everything is stable, we can port it
  to an asynchronous model using [async/await](https://github.com/scala/async),
  and then implement the relevant transport interface over AJAX.
* The indexing technique is currently very rough, and only serves as a proof
  of concept. We should develop more sophisticated, fast and flexible indices.
* The server is currently not very robust and not designed to handle multiple
  clients. It is ok just for the proof of concept, but is not in any sense an
  actual usable server.
* We could make use of something like [Catalyst](https://github.com/apache/spark/tree/master/sql/catalyst)
  to parse SQL queries and translate them into a query plan that makes use of
  random access as provided by the ORAM and indices. This would allow to
  support a basic form of SQL in an oblivious manner.
* The [oblivious data structures](https://eprint.iacr.org/2014/185.pdf) are not
  implemented yet.

The package `unicredit.lethe.async` is currently lagging behind its sync
counterpart and will need to be adapted accordingly.


##Glossary

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