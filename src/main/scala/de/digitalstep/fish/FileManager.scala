package de.digitalstep.fish

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.util.ByteString

class FileManager {

  val targetFolder = Paths.get("target")

  private[this] implicit val materializer = ActorMaterializer.create(ActorSystem("file-manager"))
  private[this] implicit val executionContext = materializer.system.dispatcher

  def load(uid: String) = {
    val source = FileIO.fromPath(targetFolder.resolve("test.txt"))
    val sink = source.runFold(ByteString(""))(_ ++ _)
    File("test.txt", "asdf".getBytes.toList)
  }


  def save(file: File): String = {
    val source = Source.fromIterator(file.bytes.iterator _).map(ByteString(_))
    val sink = FileIO.toPath(targetFolder.resolve("test.txt"))

    val result = source.runWith(sink)

    "uid"
  }

}