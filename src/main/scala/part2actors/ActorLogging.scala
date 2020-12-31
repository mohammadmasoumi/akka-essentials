package part2actors

import akka.actor.Actor

object ActorLogging extends App {

  class SimpleActorWithExplicitLogger extends Actor {
    override def receive: Receive = ???
  }

}
