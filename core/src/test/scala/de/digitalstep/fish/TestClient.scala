package de.digitalstep.fish

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object TestClient {
  def localhost(port: Int) = new TestClient("localhost", port)

  def apply(host: String = "localhost", port: Int) = new TestClient(host, port)
}

class TestClient(host: String, port: Int) extends LazyLogging {

  implicit val system = ActorSystem()

  implicit val materializer = ActorMaterializer()
  implicit val _ = system.dispatcher

  def index() = Await.result(
    request("") flatMap responseToEventualString,
    1.second
  )

  def upload(file: FileMetadata): String = {

    val eventualResponse = request(HttpRequest(
      uri = s"http://$host:$port/upload",
      headers = List(FilenameHeader(file.filename)),
      method = POST
    ))

    Await.result(eventualResponse flatMap responseToEventualString, 1.second)
  }

  def download(uid: String): FileMetadata = {
    val entity = request(s"download/$uid") map {
      case HttpResponse(OK, h, e, _) ⇒ e
      case HttpResponse(status, _, _, _) ⇒ throw new IllegalArgumentException(s"Unexpected response status code $status.")
    }

    val bytes = Await.result(entity flatMap entityToEventualByteString map (_.toList), 1.second)
    FileMetadata("test.txt")
  }

  private[this] def request(path: String): Future[HttpResponse] =
    request(HttpRequest(
      uri = s"http://$host:$port/$path"
    ))

  private[this] def request(r: HttpRequest): Future[HttpResponse] = {
    logger.info(s"Sending ${r.uri}")
    Http().singleRequest(r)
  }

  private[this] def responseToEventualString(response: HttpResponse) =
    entityToEventualByteString(response.entity) map (_.utf8String)

  private[this] def entityToEventualByteString(entity: ResponseEntity) =
    entity.dataBytes.runFold(ByteString(""))(_ ++ _)

}
