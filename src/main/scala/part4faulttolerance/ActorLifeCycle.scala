package part4faulttolerance

import akka.actor.{Actor, ActorLogging, Props}

object ActorLifeCycle extends App {

  object StartChild
  class LifeCycleActor extends Actor with ActorLogging{
    override def receive: Receive = {
      case StartChild => context.actorOf(Props[LifeCycleActor])

    }
  }


}
