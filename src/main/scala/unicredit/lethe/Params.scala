/* Copyright 2016-2019 UniCredit S.p.A.
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


case class Params(
  depth: Int,
  bucketSize: Int,
  offset: Int = 0
) {
  def nextOffset = offset + pow(2, depth + 1) - 1
  def withNextOffset = copy(offset = nextOffset)
  def numSlots = (pow(2, depth + 1) - 1) * bucketSize
}