package part4faulttolerance

import akka.actor.{Actor, ActorLogging}

object BackoffSupervisorPattern extends App {

  class FileBasePersistentActor extends Actor  with ActorLogging {
    override def receive: Receive = ???
  }

}
