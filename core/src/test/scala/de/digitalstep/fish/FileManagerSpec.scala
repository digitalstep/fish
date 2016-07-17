package de.digitalstep.fish

import org.scalatest.{FreeSpec, Matchers}

class FileManagerSpec extends FreeSpec with Matchers {
  
  "A file manager " - {
    val manager = new FileManager

    "can store files to disk" in {
      manager.save(File("test.txt", "asdf".getBytes.toList))
    }
    "can load a previously stored file from disk" in {
      manager.load("uid") shouldBe File("test.txt", "asdf".getBytes.toList)
    }
  }

}
