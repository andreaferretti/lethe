package unicredit.oram.sync

trait Client[Id, Doc] {
  def remote: Remote

  def decrypt(a: Array[Byte]): (Id, Doc)

  def encrypt(data: (Id, Doc)): Array[Byte]

  def fetchClear(n: Int) = decrypt(remote.fetch(n))

  def putClear(n: Int, data: (Id, Doc)) = remote.put(n, encrypt(data))

  def init(data: Seq[(Id, Doc)]) = remote.init(data map encrypt)
}