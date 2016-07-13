package de.digitalstep.fish

import com.typesafe.scalalogging.LazyLogging
import org.scalatest._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

class ServerSpec extends FreeSpec with Matchers {

  "A server" - {
    "when started" - {
      val server = new Server().start()
      val listenPort = Await.result(server.listenPort, 2.seconds)

      "should catch a free port" in {
        listenPort should be > 0
      }

      "should answer an HTTP request" in {
        TestClient(port = listenPort).get("") shouldBe "Hello, World!"
      }
    }
  }

}

object TestClient {
  def apply(host: String = "localhost", port: Int) = new TestClient(host, port)
}

class TestClient(host: String, port: Int) extends LazyLogging {

  import akka.actor.ActorSystem
  import akka.stream.ActorMaterializer
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.model._
  import scala.concurrent.duration._
  import akka.util.ByteString

  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()
  implicit val _ = system.dispatcher

  def get(path: String) = {
    val string = for {
      response ← request(path)
      string ← response.entity.dataBytes.runFold(ByteString(""))(_ ++ _)
    } yield string.utf8String

    Await.result(string, 1.second)
  }

  def request(path: String): Future[HttpResponse] = {
    val r = HttpRequest(
      uri = s"http://$host:$port/$path"
    )
    logger.info(s"Sending $r")
    Http().singleRequest(r)
  }

}
