package unicredit.oram

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import js.annotation.JSExport

import org.scalajs.jquery.{ jQuery => $ }


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
    $("body").text("hello")

    val bulkLoadFuture = oram.init(elements)

    // val writeFuture = Util.sequentially(elements.zipWithIndex) { case (x, i) =>
    //   println("about to write", i, x)
    //   oram.write(i, x)
    // }

    bulkLoadFuture onSuccess { case _ =>
      println("written everything")
      oram.read(1) onSuccess {
        case Some(s) =>
          $("body").text(s)
        case None =>
          println("uh?")
      }
    }
  })
}