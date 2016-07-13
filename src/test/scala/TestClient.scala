package de.digitalstep.fish

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object TestClient {
  def apply(host: String = "localhost", port: Int) = new TestClient(host, port)
}

class TestClient(host: String, port: Int) extends LazyLogging {

  implicit val system = ActorSystem()

  implicit val materializer = ActorMaterializer()
  implicit val _ = system.dispatcher

  def index() = Await.result(
    request("").flatMap(_.entity.dataBytes.runFold(ByteString(""))(_ ++ _)).map(_.utf8String),
    1.second
  )

  def upload(file: File) = {
    request("upload")
    HttpResponse(StatusCodes.Created)
  }

  def download(filename: String) = File(filename, "asdf".getBytes.toList)

  private[this] def request(path: String): Future[HttpResponse] = {
    val r = HttpRequest(
      uri = s"http://$host:$port/$path"
    )
    logger.info(s"Sending ${r.uri}")
    Http().singleRequest(r)
  }

}
