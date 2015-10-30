package unicredit.oram

import scala.scalajs.concurrent.JSExecutionContext.Implicits.queue
import scala.scalajs.js
import js.annotation.JSExport

import org.scalajs.jquery.{ jQuery => $ }


object Main extends js.JSApp {
  val elements = Array(
      "this is a secret",
      "this is secret as well",
      "strictly confidential"
    ) map (_.getBytes("UTF-8"))
  val remote = new MemoryRemote(slots = 3)
  val oram = new UnsafeORAM(remote)

  def main = $({() =>
    $("body").text("hello")

    val writeFuture = Util.sequentially(elements.zipWithIndex) { case (x, i) =>
      println("about to write", i, x)
      oram.write(i, x)
    }

    writeFuture onSuccess { case _ =>
      println("written everything")
      oram.read(1) onSuccess {
        case Some(s) =>
          $("body").text(new String(s, "UTF-8"))
        case None =>
          println("uh?")
      }
    }
  })
}