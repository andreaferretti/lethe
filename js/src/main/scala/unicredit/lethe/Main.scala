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
package unicredit.lethe

import scala.scalajs.concurrent.JSExecutionContext.Implicits.runNow
import scala.scalajs.js
import js.annotation.JSExport

import org.scalajs.jquery.{ jQuery => $ }
import async._


object Main extends js.JSApp {
  def flip[A, B](x: (A, B)) = (x._2, x._1)

  val elements = List(
      "this is a secret",
      "this is secret as well",
      "strictly confidential"
    ).zipWithIndex map flip
  val remote = new MemoryRemote
  val oram = new UnsafeORAM(remote)

  def main = $({() =>
    val future =  for {
      _ <- oram.init(elements)
      previous <- oram.read(2)
      _ <- oram.write(2, "new secret")
      bulk <- oram.read(0)
      written <- oram.read(2)
    } yield (previous, bulk, written)

    future onSuccess { case (a, b, c) =>
      $("body").text(s"$a:$b:$c")
    }

  })
}