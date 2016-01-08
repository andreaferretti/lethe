/* Copyright 2016 UniCredit S.p.A.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
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