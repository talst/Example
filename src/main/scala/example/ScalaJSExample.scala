package example

import org.scalajs.dom
import org.scalajs.dom.html

import scala.scalajs.js
import scala.scalajs.js.annotation.JSExport
import scala.util.Random


@JSExport
object ScalaJSExample {
  @JSExport
  def main(canvas: html.Canvas): Unit = {
    val renderer = canvas.getContext("2d")
      .asInstanceOf[dom.CanvasRenderingContext2D]

    canvas.width = canvas.parentElement.clientWidth
    canvas.height = 400

    renderer.font = "50px sans-serif"
    renderer.textAlign = "center"
    renderer.textBaseline = "middle"

    val obstacleGap = 200 // Gap between the approaching obstacles
    val holeSize = 50     // Size of the hole in each obstacle you must go through
    val gravity = 0.1     // Y acceleration of the player

    var playerY = canvas.height / 2.0 // Y position of the player; X is fixed
    var playerV = 0.0                 // Y velocity of the player
    // Whether the player is dead or not;
    // 0 means alive, >0 is number of frames before respawning
    var dead = 0
    // What frame this is; used to keep track
    // of where the obstacles should be positioned
    var frame = -50
    // List of each obstacle, storing only the Y position of the hole.
    // The X position of the obstacle is calculated by its position in the
    // queue and in the current frame.
    val obstacles = collection.mutable.Queue.empty[Int]

    def runLive() = {
      frame += 2

      if (frame >= 0 && frame % obstacleGap ==0)
        obstacles.enqueue(Random.nextInt(canvas.height - 2 * holeSize) + holeSize)
      if (obstacles.length > 7) {
        obstacles.dequeue()
        frame -= obstacleGap
      }

      playerY += playerV
      playerV += gravity

      renderer.fillStyle = "darkblue"
      for ((holeY, i) <- obstacles.zipWithIndex) {
        val holeX = i * obstacleGap - frame + canvas.width
        renderer.fillRect(x = holeX, y = 0, w = 5, h = holeY - holeSize)
        renderer.fillRect(
          x = holeX,
          y = holeY + holeSize,
          w = 5,
          h = canvas.height - holeY - holeSize
        )

        if (math.abs(holeX - canvas.width / 2) < 5 && math.abs(holeY - playerY) > holeSize) dead = 50
      }
      renderer.fillStyle = "darkgreen"
      renderer.fillRect(canvas.width /2 - 5, playerY - 5, 10, 10)

      if (playerY < 0 || playerY > canvas.height) dead = 50
    }

    def runDead() = {
      playerY = canvas.height / 2
      playerV = 0
      frame = -50
      obstacles.clear()
      dead -= 1
      renderer.fillStyle = "darkred"
      renderer.fillText("Game Over", canvas.width / 2, canvas.height / 2)
    }

    def run() = {
      renderer.clearRect(x = 0, y = 0, w = canvas.width, h = canvas.height)
      if (dead > 0) runDead()
      else runLive()
    }

    dom.setInterval(run _, 20)

    canvas.onclick = (e: dom.MouseEvent) => {
      playerV -= 5
    }
  }
}
