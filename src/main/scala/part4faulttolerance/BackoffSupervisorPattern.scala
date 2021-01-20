package part4faulttolerance

import akka.actor.{Actor, ActorLogging}

import scala.io.Source

object BackoffSupervisorPattern extends App {

  case object ReadFile

  class FileBasePersistentActor extends Actor with ActorLogging {
    override def receive: Receive = handleIO()

    def handleIO(dataSource: Source = null): Receive = {

    }


  }

}
