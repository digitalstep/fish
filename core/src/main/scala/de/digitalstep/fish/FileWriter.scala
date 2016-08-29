package de.digitalstep.fish

import akka.stream.IOResult
import akka.stream.scaladsl.Source
import akka.util.ByteString

import scala.concurrent.Future

trait FileWriter {

  def write(filename: String, source: Source[ByteString, Any]): Future[IOResult] =
    write(new FileSource(FileMetadata(filename), source))

  def write(f: FileSource): Future[IOResult]

}
