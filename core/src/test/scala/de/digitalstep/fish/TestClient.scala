package de.digitalstep.fish

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration._
import scala.concurrent.{Await, Awaitable, Future}


object TestClient {
  def localhost(port: Int) = new TestClient("localhost", port)

  def apply(host: String = "localhost", port: Int) = new TestClient(host, port)
}

class TestClient(host: String, port: Int) extends LazyLogging {

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val _ = system.dispatcher

  def index() = awaitResult {
    request("") flatMap responseToEventualString
  }

  def upload(file: FileSource) = awaitResult {
    HttpRequest(
      uri = s"http://$host:$port/upload",
      headers = FilenameHeader(file.metadata.filename) :: Nil,
      method = POST,
      entity = HttpEntity(`application/octet-stream`, file.source)
    )
  }


  def download(uid: String): FileMetadata = {
    val entity = request(s"download/$uid") map {
      case HttpResponse(OK, h, e, _) ⇒ e
      case HttpResponse(status, _, _, _) ⇒ throw new IllegalArgumentException(s"Unexpected response status code $status.")
    }

    val bytes = awaitResult {
      entity flatMap entityToEventualByteString map (_.toList)
    }
    FileMetadata("test.txt")
  }

  private[this] def awaitResult(r: HttpRequest) = Await.result(request(r), 1.second)

  private[this] def request(path: String): Future[HttpResponse] =
    request(HttpRequest(
      uri = s"http://$host:$port/$path"
    ))

  private[this] def awaitResult[T](awaitable: Awaitable[T]): T = Await.result(awaitable, 1.second)

  private[this] def request(r: HttpRequest): Future[HttpResponse] = {
    logger.info(s"Sending ${r}")
    Http().singleRequest(r)
  }

  private[this] def responseToEventualString(response: HttpResponse) =
    entityToEventualByteString(response.entity) map (_.utf8String)

  private[this] def entityToEventualByteString(entity: ResponseEntity) =
    entity.dataBytes.runFold(ByteString(""))(_ ++ _)

}
