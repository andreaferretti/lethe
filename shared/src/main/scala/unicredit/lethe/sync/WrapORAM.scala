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
package sync


class WrapORAM[K, V, K1 <: K, V1 <:V, Id, Doc] (
  inner: PathORAM[K, V, K1, V1],
  bijId: Bijection[Id, K1],
  bijDoc: Bijection[Doc, V1]
) extends ORAM[Id, Doc] {
  override def read(id: Id) = bijDoc.to(inner.read(bijId.from(id)))

  override def write(id: Id, doc: Doc) =
    inner.write(bijId.from(id), bijDoc.from(doc))

  override def init = inner.init
}