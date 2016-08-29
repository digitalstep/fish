package de.digitalstep.fish

import java.nio.file.{Path, Paths}

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server.Directives._
import akka.stream.scaladsl.{FileIO, Source}
import akka.stream.{ActorMaterializer, IOResult}
import akka.util.ByteString
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.{ExecutionContext, Future}

trait UploadRoute extends LazyLogging {
  import UploadDirectives._

  implicit val fileWriter: FileWriter
  implicit val materializer: ActorMaterializer
  implicit val executionContext: ExecutionContext


  val upload = (post & path("upload")) {
    extractDataBytes { bytes ⇒
      extractFilename { filename ⇒
        onComplete(fileWriter.write(filename, bytes)) { _ ⇒
          complete {
            HttpResponse(
              status = Created,
              headers = FilenameHeader(filename) :: Nil
            )
          }
        }
      }
    }
  }


  private[this] def save(bytes: Source[ByteString, Any]) = new ToFile(bytes)

  private[this] class ToFile(source: Source[ByteString, Any]) {
    def to(p: Path): Future[IOResult] = source.runWith(FileIO.toPath(p))
  }

}

/**
  * Custom directives used in file uploads
  */
object UploadDirectives {
  val extractDataBytes = extract(_.request.entity.dataBytes)
  val extractFilename = headerValueByType[FilenameHeader]().map(_.value)
}