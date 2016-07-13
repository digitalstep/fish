package de.digitalstep.fish

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

class Server {

  private[this] implicit val system = ActorSystem("server")
  private[this] implicit val materializer = ActorMaterializer()

  private[this] implicit val executionContext = system.dispatcher

  val listenPort = 1

  val route =
    path("") {
      get {
        complete(HttpEntity(ContentTypes.`text/plain(UTF-8)`, "Hello, World!"))
      }
    }

  def start() = new RunningServer(Http().bindAndHandle(route, "localhost", 0))

}

class RunningServer(binding: Future[ServerBinding])(implicit executionContext: ExecutionContext) {

  def listenPort = binding map (_.localAddress.getPort)

}
