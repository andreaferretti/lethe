package unicredit.oram


case class Params(
  depth: Int,
  bucketSize: Int,
  offset: Int = 0
) {
  def nextOffset = offset + pow(2, depth + 1) - 1
  def withNextOffset = copy(offset = nextOffset)
  def numSlots = (pow(2, depth + 1) - 1) * bucketSize
}