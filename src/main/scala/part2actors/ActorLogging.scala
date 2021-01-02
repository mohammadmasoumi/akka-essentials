package part2actors

import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.event.Logging

object ActorLogging extends App {

  // #1 - explicit logging
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
  val actor1 = system.actorOf(Props[SimpleActorWithExplicitLogger], "loggingActor")
  actor1 ! "Logging a simple message"

  // #2 - ActorLogging
  class ActorWithLogging extends Actor with ActorLogging {
    override def receive: Receive = {
      case (a, b) => log.info("Two things: {} and {}", a, b) // Two things: 2 and 3
      case message: String => log.info(s"message: $message")
    }
  }
  val actor2 = system.actorOf(Props[ActorWithLogging], "actorWithLogging")
  actor2 ! "Logging a simple message"


}
