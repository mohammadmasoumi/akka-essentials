package part4faulttolerance

import java.io.File

import akka.actor.{Actor, ActorLogging}

import scala.io.Source

object BackoffSupervisorPattern extends App {

  case object ReadFile

  class FileBasePersistentActor extends Actor with ActorLogging {

    override def preStart(): Unit =
      log.info("Persistent actor starting")


    override def receive: Receive = handleIO()

    def handleIO(dataSource: Source = null): Receive = {
      case ReadFile =>
        if (dataSource == null) {
          val newSource = Source.fromFile(new File("src/main/resources/testfiles/important.txt"))
          context.become(handleIO(newSource))
          log.info("I've read just some important data: " + newSource.getLines().toList)
        }
    }


  }

}
