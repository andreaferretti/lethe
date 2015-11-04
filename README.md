Implementation of https://eprint.iacr.org/2013/280.pdf

TODO:

* Move server in a separate project
* Add backends to server (memory, Redis, LevelDB...)
* Recursive ORAM
* Add search on top of key/value store
* Async implementation
* Compile SQL queries to sequences of get/search operations