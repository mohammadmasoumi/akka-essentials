package part2actors

import akka.actor.{Actor, ActorSystem, Props}
import akka.event.Logging

object ActorLogging extends App {
  class SimpleActorWithExplicitLogger extends Actor {
    val logger = Logging(context.system, this)

    override def receive: Receive = {
      case message => // log it?
        /*
         4 levels of logging:
          1. DEBUG
          2. INFO
          3. WARNING/WARN
          4. ERROR
         */
        logger.info(s"message: $message")
    }
  }
  val system = ActorSystem("LoggingDemo")
  val actor = system.actorOf(Props[SimpleActorWithExplicitLogger], "loggingActor")
  actor ! "Logging a simple message"

}
