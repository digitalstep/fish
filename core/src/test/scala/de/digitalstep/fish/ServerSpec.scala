package de.digitalstep.fish

import akka.stream.StreamTcpException
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.util.{Try, Success, Failure}

class ServerSpec extends FreeSpec with Matchers {

  "A server" - {

    "when started" - {
      val server = Await.result(new Server().start(), 2.seconds)

      val testClient = TestClient.localhost(server.listenPort)
      val testFile = FileMetadata("test.txt")
      val testUid = "uid"

      "should catch a free port" in {
        server.listenPort should be > 0
      }

      "should answer an HTTP request" in {
        testClient.index() shouldBe "Hello, World!"
      }

      "should accept a file upload" in {
        testClient.upload(testFile) shouldBe testUid
      }

      "deliver a previously uploaded file" in {
        testClient.download(testUid) shouldBe testFile
      }

      "can be stopped" in {
        Await.ready(server.stop(), 2.seconds)
      }

    }

    "when stopped" - {
      val server = Await.result(new Server().start(), 2.seconds)

      "should not accept TCP connections anymore" in {
        val testClient = TestClient.localhost(server.listenPort)
        testClient.index()

        Await.ready(server.stop(), 2.seconds)
        intercept[StreamTcpException] {
          testClient.index()
        }
      }

    }

  }

}
