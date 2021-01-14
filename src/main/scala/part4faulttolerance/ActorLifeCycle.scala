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

  parent ! StartChild
  parent ! PoisonPill

  /**
   * restart
   */

  object Fail

  class Parent extends Actor with ActorLogging {
    val child = context.actorOf(Props[Child], "supervisedChild")

    override def receive: Receive = {

    }
  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case Fail =>
        log.warning("child will fail now!")
        throw new RuntimeException("I failed")
    }
  }


}
