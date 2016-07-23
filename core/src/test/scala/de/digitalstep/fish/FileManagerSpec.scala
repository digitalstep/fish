package de.digitalstep.fish

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.testkit.scaladsl.TestSink
import akka.testkit.TestKit
import akka.util.ByteString
import org.scalatest.{BeforeAndAfterAll, FreeSpec, FreeSpecLike, Matchers}

class FileManagerSpec extends TestKit(ActorSystem("FileManagerSpec"))
  with FreeSpecLike
  with Matchers
  with BeforeAndAfterAll {

  implicit val materializer = ActorMaterializer()

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }

  "A file manager " - {
    val manager = new FileManager

    "can store files to disk" in {
      manager.save(FileMetadata("test.txt")) shouldBe "uid"
    }

    "can load a previously stored file from disk" in {
      val (metadata, source) = manager.load("uid")
      source.
        runWith(TestSink.probe[ByteString]).
        request(1).
        expectNext(ByteString("This is a test\n")).
        cancel()


      metadata shouldBe FileMetadata("test.txt")
    }
  }

}
