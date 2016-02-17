package webpage

import org.scalajs.dom
import org.scalajs.dom.html
import org.scalajs.dom.html._
import rx.core.{Obs, Rx, Var}

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scalatags.JsDom.all._

@JSExport
object HelloWorld0 {


  implicit def jsAnyFrag[T <: js.Any](jsAny: T): Frag = {
    js.JSON.stringify(jsAny)
  }

  implicit def rxFrag[T](r: Rx[T])(implicit toFrag: T => Frag): Frag = {
    def rSafe: dom.Node = span(r()).render
    var last = rSafe
    Obs(r, skipInitial = true) {
      val newLast = rSafe
      js.Dynamic.global.last = last
      last.parentNode.replaceChild(newLast, last)
      last = newLast
    }
    last
  }

  val vector = Var(Vector.empty[(TextArea, Rx[String])])

  val addButton = button(
    "add"
  ).render

  addButton.onclick = (e: dom.Event) => {
    val currentList = vector()
    val txt = Var("")
    val txtInput = textarea.render
    txtInput.onkeyup = (e: dom.Event) => {
      txt() = txtInput.value
    }

    val newVector = currentList :+(txtInput, txt)

    vector() = newVector
  }

  val removeButton = button(
    "remove"
  ).render

  removeButton.onclick = (e: dom.Event) => {
    val currentList = vector()
    val newList = currentList.take(currentList.size - 1)
    vector() = newList
  }

  @JSExport
  def main(container: html.Div) = {

    val txt = Var("")
    val numChars = Rx {
      txt().length
    }
    val numWords = Rx {
      txt().split(' ').count(_.length > 0)
    }

    val avgWordLength = Rx {
      txt().count(_ != ' ') * 1.0 / numWords()
    }

    val txtInput = textarea.render
    txtInput.onkeyup = (e: dom.Event) => {
      txt() = txtInput.value
    }

    val json = Rx {
      if (txt().nonEmpty) Some(js.Dictionary("name" -> txt()))
      else None
    }

    val lis = Rx {
      vector().map { case (box, text) => li(box) }
    }

    val lis2 = Rx {
      vector().map { case (box, text) =>
        li(Rx {
          if (text().nonEmpty) Some(js.Dictionary("name" -> text()))
          else None
        })
      }
    }

    container.appendChild(
      div(
        txtInput,
        addButton,
        removeButton,
        ul(lis),
        ul(lis2),
        ul(
          li("Chars: ", numChars),
          li("Words: ", numWords),
          li("Word Length: ", avgWordLength),
          li(json)
        )
      ).render
    )
  }
}
