package unicredit.oram

import scala.concurrent.{ Future, ExecutionContext }


trait TrivialORAM[Id, Doc] extends ORAM[Id, Doc] with Client[Id, Doc] {
  override def readAndRemove(id: Id) =
    remote.capacity flatMap { n =>
      println(n)
      (0 until n).foldLeft(Future(None: Option[Doc])) { (f, i) =>
        for {
          (uid, doc) <- fetchClear(i)
          next <- if (id == uid) Future(Some(doc)) else f
          data = if (id == uid) (uid, empty) else (uid, doc)
          _ <- putClear(i, data)
        } yield next
      }
    }

  override def add(id: Id, doc: Doc) =
    remote.capacity flatMap { n =>
      (0 until n).foldLeft(Future(())) { (f, i) =>
        for {
          (uid, udoc) <- fetchClear(i)
          data = if (udoc == empty) (id, doc) else (uid, udoc)
          wait <- putClear(i, data)
        } yield wait
      }
    }
}