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