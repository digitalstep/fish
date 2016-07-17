package de.digitalstep.fish.remoting

import akka.actor._
import org.scalatest._

import scala.concurrent.Await
import scala.concurrent.duration._

class RemotingSpec extends FreeSpec with Matchers {

  val system = ActorSystem("remoting")

  "An actor" - {
    val b = system.actorOf(Props(classOf[B]))

    "with remoting enabled" - {

      "should accept messages from remote actors" in {
        val a = system.actorOf(Props(classOf[A], b))
      }
    }
  }


  Thread.sleep(2000)
  Await.ready(system.terminate(), 3.seconds)

}

class A(x: ActorRef) extends Actor {

  override def preStart() = self ! "Ping!"

  def receive = {
    case "Ping!" ⇒
      println("Got Ping!")
      x ! "Pong!"
  }
}

class B extends Actor {
  def receive = {
    case x ⇒ println("got " + x)
  }
}
