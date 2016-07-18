package de.digitalstep.fish

import akka.stream.scaladsl.Source
import akka.util.ByteString

/**
  * Combines metadata and a streaming source.
  */
class FileSource(val metadata: FileMetadata, val source: Source[ByteString, Any]) {

}
