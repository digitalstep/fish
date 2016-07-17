package de.digitalstep.fish.remoting

import akka.actor.{Actor, ActorSystem}

object SimpleActor extends App {

  val system = ActorSystem()

  class A extends Actor {
    def receive: Receive = {
      case _ ⇒ sender() ! "Pong!"
    }
  }

}