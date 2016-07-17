package de.digitalstep.fish

import akka.http.scaladsl.model.headers.RawHeader
import org.scalatest.{FreeSpec, Matchers}

class FilenameHeaderSpec extends FreeSpec with Matchers {

  "A filename header" - {
    "should hold a filename" in {
      val FilenameHeader(filename) = FilenameHeader("test.txt")
      filename should ===("test.txt")
    }

    "should have X-Filename as name" in {
      val RawHeader(k, v) = FilenameHeader("filename")

      k should ===("X-Filename")
    }
  }

}
