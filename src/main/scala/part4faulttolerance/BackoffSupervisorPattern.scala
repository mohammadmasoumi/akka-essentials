package part4faulttolerance

import akka.actor.{Actor, ActorLogging}

object BackoffSupervisorPattern extends App {

  case object ReadFile
  class FileBasePersistentActor extends Actor  with ActorLogging {
    override def receive: Receive = ???
  }

}
