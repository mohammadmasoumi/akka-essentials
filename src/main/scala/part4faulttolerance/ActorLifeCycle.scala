package part4faulttolerance

import akka.actor.{Actor, ActorLogging}

object ActorLifeCycle extends App {

  class LifeCycleActor extends Actor with ActorLogging{
    override def receive: Receive = ???
  }


}
