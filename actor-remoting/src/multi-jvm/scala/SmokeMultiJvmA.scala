import akka.actor.ActorDSL._
import akka.actor.ActorSystem

object SmokeMultiJvmA extends App {

  implicit val system = ActorSystem("A")

  actor(new Act {
    become {
      case "hello" ⇒
        println("hello")
        sender() ! "hi"
        context.system.terminate()
    }
  })

}
