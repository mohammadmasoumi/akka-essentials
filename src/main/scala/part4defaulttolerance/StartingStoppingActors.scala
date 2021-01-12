package part4defaulttolerance

import akka.actor.{Actor, ActorLogging, ActorSystem}

class StartingStoppingActors extends App {

  val system = ActorSystem("StoppingActorDemo")


  class Parent extends Actor {
    override def receive: Receive = ???
  }

  class Child extends Actor  with ActorLogging{
    override def receive: Receive = ???
  }

}
