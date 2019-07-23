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


trait TrivialORAM[Id, Doc] extends ORAM[Id, Doc] with Client[Id, Doc] {
  override def readAndRemove(id: Id) = {

    remote.capacity flatMap { n =>
      (0 until n).foldLeft(Future(None: Option[Doc])) { (f, i) =>
        for {
          (uid, doc) <- fetchClear(i)
          next <- if (id == uid) Future(Some(doc)) else f
          data = if (id == uid) (uid, empty) else (uid, doc)
          _ <- putClear(i, data)
        } yield next
      }
    }
  }

  override def add(id: Id, doc: Doc) =
    remote.capacity flatMap { n =>
      (0 until n).foldLeft(Future(())) { (f, i) =>
        for {
          (uid, udoc) <- fetchClear(i)
          data = if (id == uid) (uid, doc) else (uid, udoc)
          _ <- putClear(i, data)
        } yield ()
      }
    }
}