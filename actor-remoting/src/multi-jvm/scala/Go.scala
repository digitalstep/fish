import akka.actor.{Actor, ActorPath, ActorSystem, Props}
import akka.actor.ActorDSL._

object Go extends App {

  implicit val system = ActorSystem()

  val a = actor(new Act {
    become {
      case p: ActorPath ⇒ context.system.actorSelection(p) ! "hello"
      case "bye" ⇒ context.system.terminate()
      case s ⇒ println(s)
    }
  })

  a ! ActorPath.fromString("akka.tcp://A@127.0.0.1:9991/user/$a")

}
