package de.digitalstep.fish

import akka.stream.testkit.scaladsl.TestSink
import akka.util.ByteString
import org.scalatest.{FreeSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io._

class FileManagerSpec extends FreeSpec with Matchers {

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
        expectNext(ByteString("T")).
        cancel()


      metadata shouldBe FileMetadata("test.txt")
    }
  }

}
