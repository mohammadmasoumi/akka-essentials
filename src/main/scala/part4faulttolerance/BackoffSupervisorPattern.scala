package part4faulttolerance

import java.io.File

import akka.actor.SupervisorStrategy.Stop
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props}
import akka.pattern.{Backoff, BackoffSupervisor}

import scala.concurrent.duration._
import scala.io.Source

object BackoffSupervisorPattern extends App {

  case object ReadFile

  class FileBasedPersistentActor extends Actor with ActorLogging {

    override def preStart(): Unit =
      log.info("Persistent actor starting.")

    override def postStop(): Unit =
      log.warning("Persistent actor has stopped!")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      log.warning("Persistent actor restarting.")

    override def receive: Receive = handleIO()

    def handleIO(dataSource: Source = null): Receive = {
      case ReadFile =>
        if (dataSource == null) {
          val newSource = Source.fromFile(new File("src/main/resources/testfiles/important_data.txt"))
          context.become(handleIO(newSource))
          log.info("I've read just some important data: " + newSource.getLines().toList)
        }
    }
  }

  val system = ActorSystem("BackoffSupervisorDemo")
  //  val simpleActor = system.actorOf(Props[FileBasedPersistentActor], "simpleActor")
  //  simpleActor ! ReadFile

  val simpleSupervisorProps = BackoffSupervisor.props(
    Backoff.onFailure(
      Props[FileBasedPersistentActor],
      "simpleBackoffAccor",
      3 seconds, // then 6s, 12s, 24s, no more(30s)
      30 seconds,
      0.2 // gap between restarting
    )
  )

  //  val simpleBackoffSupervisor = system.actorOf(simpleSupervisorProps, "simpleSupervisor")
  //  simpleBackoffSupervisor ! ReadFile

  /**
   * simpleSupervisor
   *  - child called simpleBackoffActor (props of type FileBasedPersistentActor)
   *  - supervisor strategy is the default one (restarting on everything)
   *   - first attempt after 3 seconds.
   *   - next attempt is 2x the previous attempt.
   */

  val stoppedSupervisorProps = BackoffSupervisor.props(
    Backoff.onStop(
      Props[FileBasedPersistentActor],
      "stoppedBackoffActor",
      3 seconds,
      30 seconds,
      0.2
    ).withSupervisorStrategy(
      OneForOneStrategy() {
        case _ => Stop
      }
    )
  )

  //  val stopSupervisor = system.actorOf(stoppedSupervisorProps, "stopSupervisor")
  //  stopSupervisor ! ReadFile

  class EagerFileBasedPersistentActor extends FileBasedPersistentActor {
    override def preStart(): Unit = {
      log.info("Eager actor starting.")
      val newSource = Source.fromFile(new File("src/main/resources/testfiles/important_data.txt"))
      context.become(handleIO(newSource))
    }
  }

//  val eagerActor = system.actorOf(Props[EagerFileBasedPersistentActor], "eagerActor")
  // default strategy for `ActorInitializationException` => STOP

  val repeatedSupervisorProps = BackoffSupervisor.props(
    Backoff.onStop(
      Props[EagerFileBasedPersistentActor],
      "eagerBackoffActor",
      1 second,
      30 seconds,
      0.1
    )
  )

  val repeatedSupervisor = system.actorOf(repeatedSupervisorProps, "repeatedSupervisor")




}
