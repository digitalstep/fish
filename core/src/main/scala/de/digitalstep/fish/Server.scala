package de.digitalstep.fish

import akka.actor.{ActorSystem, Terminated}
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.Future

class Server extends UploadRoute {

  implicit val system = ActorSystem("server")
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = system.dispatcher

  implicit val fileWriter: FileWriter = new FileManager

  val listenPort = 1

  val route =
  helloWorld ~
  upload ~
  download

  lazy val download = path("download" / "uid") {
    get {
      complete {
        HttpResponse(
        status = OK,
        entity = HttpEntity(`application/octet-stream`, "asdf".getBytes)
        )
      }
    }
  }

  lazy val helloWorld = pathSingleSlash {
    get {
      complete {
        HttpResponse(
        status = OK,
        entity = HttpEntity(`text/plain(UTF-8)`, "Hello, World!")
        )
      }
    }
  }


  def start() = Http().bindAndHandle(route, "localhost", 0).map(b ⇒ new RunningServer(b))

}

/**
  * Can be stopped and queried for the port the server is currently listening on
  */
class RunningServer(binding: ServerBinding)(implicit val system: ActorSystem) {
  private[this] implicit val _ = system.dispatcher

  /**
    * Terminates server and ActorSystem
    *
    * @return a future which completes after the ActorSystem has been shut down
    */
  def stop(): Future[Terminated] = for {
    b ← binding.unbind()
    t ← system.terminate()
  } yield t

  def listenPort: Int = binding.localAddress.getPort

}
