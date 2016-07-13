import org.scalatest._

class FileSpec extends FreeSpec with Matchers {

  "A file" - {
    "should have a name" in {
      File("filename.txt").name shouldBe "filename.txt"
    }
  }

}
