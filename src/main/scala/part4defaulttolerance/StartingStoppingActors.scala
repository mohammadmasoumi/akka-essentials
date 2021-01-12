package part4defaulttolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem}

class StartingStoppingActors extends App {

  val system = ActorSystem("StoppingActorDemo")


  class Parent extends Actor with ActorLogging {
    override def receive: Receive = withChildren(Map())

    def withChildren(children: Map[String, ActorRef]): Receive = {

    }
  }

  object Parent {

    case class StartChild(name: String)

    case class StopChild(name: String)

    case object Stop

  }

  class Child extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

}
