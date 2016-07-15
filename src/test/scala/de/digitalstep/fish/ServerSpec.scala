package de.digitalstep.fish

import akka.stream.StreamTcpException
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

class ServerSpec extends FreeSpec with Matchers {

  "A server" - {

    "when started" - {
      val server = new Server().start()
      val listenPort = Await.result(server.listenPort, 2.seconds)

      val testClient = TestClient(port = listenPort)
      val testFile = File("test.txt", "asdf".getBytes().toList)
      val testUid = "uid"

      "should catch a free port" in {
        listenPort should be > 0
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
      val server = new Server().start()

      "should not accept TCP connections anymore" in {
        val testClient = TestClient(port = Await.result(server.listenPort, 2.seconds))
        Await.ready(server.stop(), 2.seconds)
        intercept[StreamTcpException] {
          testClient.index()
        }
      }

    }

  }

}
