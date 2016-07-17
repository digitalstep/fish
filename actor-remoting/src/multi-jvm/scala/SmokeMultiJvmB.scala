import akka.actor.ActorDSL._
import akka.actor.ActorSystem

object SmokeMultiJvmB extends App {

  implicit val system = ActorSystem("B")

  actor(new Act {
    become {
      case "bye" ⇒ context.system.terminate()
      case "hi!" ⇒ println("hi!")
      case s: String ⇒
        println("go " + s)
        context.system.actorSelection(s) ! "Hello"
    }

  })

}
