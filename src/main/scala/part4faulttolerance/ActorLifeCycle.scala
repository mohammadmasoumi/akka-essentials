package part4faulttolerance

import akka.actor.{Actor, ActorLogging, ActorSystem, PoisonPill, Props}

object ActorLifeCycle extends App {

  object StartChild

  class LifeCycleActor extends Actor with ActorLogging {

    override def preStart(): Unit = log.info("I am starting.")

    override def postStop(): Unit = log.info("I have stopped!")

    override def receive: Receive = {
      case StartChild => context.actorOf(Props[LifeCycleActor], "child")
    }
  }

  val system = ActorSystem("LifeCycleActor")
  val parent = system.actorOf(Props[LifeCycleActor], "parent")

//  parent ! StartChild
//  parent ! PoisonPill

  /**
   * restart
   */

  object Fail
  object FailChild

  class Parent extends Actor with ActorLogging {
    private val child = context.actorOf(Props[Child], "supervisedChild")

    override def receive: Receive = {
      case FailChild =>
        child ! Fail
    }
  }

  class Child extends Actor with ActorLogging {

    override def preStart(): Unit = log.info("supervisedChild started.")

    override def postStop(): Unit = log.info("supervisedChild have stopped.")

    override def preRestart(reason: Throwable, message: Option[Any]): Unit =
      log.info(s"supervisedActor is restarting because of $reason")

    override def aroundPostRestart(reason: Throwable): Unit =
      log.info("supervisedActor restarted.")

    override def receive: Receive = {
      case Fail =>
        log.warning("child will fail now!")
        throw new RuntimeException("I failed")
    }
  }


}
