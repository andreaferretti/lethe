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


object Util {
  def foldFutures[A, B, C](xs: Seq[A], f: A => Future[B], empty: C,
    combine: (C, B) => C)(implicit ec: ExecutionContext): Future[C] =
      xs.foldLeft(Future(empty)) { (future, x) =>
        for {
          c <- future
          b <- f(x)
        } yield combine(c, b)
      }

  def sequentially[A](xs: Seq[A])(f: A => Future[Unit])
    (implicit ec: ExecutionContext) =
      foldFutures(xs, f, (), { (a: Unit, b: Unit) => () })
}