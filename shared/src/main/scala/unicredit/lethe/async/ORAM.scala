package unicredit.lethe.async

import scala.concurrent.{ Future, ExecutionContext }


trait ORAM[Id, Doc] {
  implicit def ec: ExecutionContext
  def empty: Doc
  def readAndRemove(id: Id): Future[Option[Doc]]
  def add(id: Id, doc: Doc): Future[Unit]

  def read(id: Id) = for {
    data <- readAndRemove(id)
    doc = data getOrElse empty
    _ <- add(id, doc)
  } yield data

  def write(id: Id, doc: Doc) = for {
    _ <- readAndRemove(id)
    _ <- add(id, doc)
  } yield ()
}