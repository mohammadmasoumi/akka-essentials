package part4faulttolerance

import akka.actor.{Actor, ActorLogging, Props}

object ActorLifeCycle extends App {

  object StartChild
  class LifeCycleActor extends Actor with ActorLogging{

    override def preStart(): Unit = log.info("I am starting.")
    override def receive: Receive = {
      case StartChild => context.actorOf(Props[LifeCycleActor], "child")

    }
  }


}
