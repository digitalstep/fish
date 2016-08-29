package de.digitalstep.fish

import akka.http.scaladsl.model.StatusCodes._
import akka.dispatch.Futures
import akka.stream.StreamTcpException
import akka.stream.scaladsl.Source
import akka.util.ByteString
import de.digitalstep.fish.testkit.TestFiles
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

class ServerSpec extends FreeSpec with Matchers with BeforeAndAfterAll {

  val (server, testClient) = testServerAndClient


  override def afterAll() = {
    Seq(
      server.stop(),
      testClient.system.terminate()
    ) foreach {
      Await.ready(_, 2.seconds)
    }
  }

  "A server" - {

    "when started" - {

      val testFile = FileMetadata("test.txt")
      val testUid = "uid"

      "should catch a free port" in {
        server.listenPort should be > 0
      }

      "should answer an HTTP request" in {
        testClient.index() shouldBe "Hello, World!"
      }

      "should accept a file upload" in {
        testClient.upload(new FileSource(testFile, Source.single(ByteString("Test")))).status shouldBe Created
      }

      "deliver a previously uploaded file" in {
        testClient.download(testUid) shouldBe testFile
      }

    }

    "when stopped" - {
      val (server, testClient) = testServerAndClient
      Await.ready(server.stop(), 2.seconds)

      "should not accept TCP connections anymore" in {
        intercept[StreamTcpException] {
          testClient.index()
        }
        Await.ready(testClient.system.terminate(), 2.seconds)
      }

    }

  }

  private[this] def testServerAndClient = {
    val server = Await.result(new Server().start(), 2.seconds)
    val client = TestClient.localhost(server.listenPort)
    (server, client)
  }

}
