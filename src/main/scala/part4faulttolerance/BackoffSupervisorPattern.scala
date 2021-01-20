package part4faulttolerance

import akka.actor.Actor

object BackoffSupervisorPattern extends App {

  class FileBasePersistentActor extends Actor {
    override def receive: Receive = ???
  }

}
