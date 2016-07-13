package de.digitalstep.fish

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.Future

class Server {

  implicit val system = ActorSystem("server")
  private[this] implicit val materializer = ActorMaterializer()

  private[this] implicit val executionContext = system.dispatcher

  val listenPort = 1

  val route =
    path("") {
      get {
        complete {
          HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello, World!")
        }
      }
    } ~
    path("download" / "test.txt") {
      get {
        complete {
          HttpEntity(ContentTypes.`application/octet-stream`, "asdf".getBytes)
        }
      }
    }

  def start() = new RunningServer(Http().bindAndHandle(route, "localhost", 0))

}

class RunningServer(binding: Future[ServerBinding])(implicit val system: ActorSystem) {
  private[this] implicit val _ = system.dispatcher

  def stop() = {
    val map = binding.flatMap(_.unbind())
    map.onComplete(_ ⇒ system.terminate())
    map
  }

  def listenPort = binding map (_.localAddress.getPort)

}
