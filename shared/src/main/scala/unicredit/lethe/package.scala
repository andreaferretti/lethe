package unicredit

package object lethe {
  def pow(n: Int, k:Int): Int = k match {
    case kk if kk < 0  => 0  // error
    case 0 => 1
    case 1 => n
    case 2 => n*n
    case kk  if kk % 2 == 0  => pow(pow(n, k/2), 2)
    case _  => pow(n, k/2)*pow(n, (k+1)/2)
  }
}