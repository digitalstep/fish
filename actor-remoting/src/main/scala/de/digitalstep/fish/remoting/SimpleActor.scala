package de.digitalstep.fish.remoting

import akka.NotUsed
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.stream.scaladsl.Source
import akka.util.ByteString
import akka.pattern.pipe
import akka.stream.ActorMaterializer

object SimpleActor extends App {

  implicit val system = ActorSystem()

  system.actorOf(Props(classOf[A]))

}

class A extends Actor with ActorLogging {
  implicit val materializer = ActorMaterializer()
  implicit val executionContext = context.dispatcher

  def receive = {
    case StreamMessage(title, source) ⇒
      log.info(s"Received $title")
      pipe(source.runFold(ByteString(""))(_ ++ _) map LogMessage) to self
    case _ ⇒ sender() ! "Pong!"
  }

}

case class StreamMessage(title: String, source: Source[ByteString, NotUsed])

case class LogMessage(s: ByteString)