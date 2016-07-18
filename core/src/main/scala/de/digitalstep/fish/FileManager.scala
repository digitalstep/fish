package de.digitalstep.fish

import java.io.InputStream
import java.nio.file.{Path, Paths}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

class FileManager {

  val targetFolder = Paths.get("target")

  private[this] implicit val materializer = ActorMaterializer.create(ActorSystem("file-manager"))
  private[this] implicit val executionContext = materializer.system.dispatcher

  def load(uid: String) = {
    val metadata = FileMetadata("test.txt")
    val source = StreamConverters.fromInputStream(() ⇒ getClass.getResourceAsStream("/testfile.txt"))
    (metadata, source)
  }


  def save(file: FileMetadata): String = {
    val source = Source.fromIterator(() ⇒ "asdf".getBytes.iterator).map(ByteString(_))
    val sink = FileIO.toPath(targetFolder.resolve("test.txt"))

    val result = source.runWith(sink)

    "uid"
  }

}