package de.digitalstep.fish

import akka.actor.ActorSystem
import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes._
import akka.stream.{ActorMaterializer, StreamTcpException}
import akka.util.ByteString
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

class ServerSpec extends FreeSpec with Matchers {

  "A server" - {

    "when started" - {
      val server = new Server().start()
      val listenPort = Await.result(server.listenPort, 2.seconds)

      def testClient = TestClient(port = listenPort)
      val testFile = File("test.txt", "asdf".getBytes().toList)

      "should catch a free port" in {
        listenPort should be > 0
      }

      "should answer an HTTP request" in {
        testClient.index() shouldBe "Hello, World!"
      }

      "should accept a file upload" in {
        testClient.upload(testFile) shouldBe HttpResponse(Created)
      }

      "deliver a previously uploaded file" in {
        testClient.download(testFile.filename) shouldBe testFile
      }

      "can be stopped" in {
        Await.ready(server.stop(), 2.seconds)
      }

      "and should not accept TCP connections anymore" in {
        intercept[StreamTcpException] {
          testClient.index()
        }
      }
    }
  }

}
