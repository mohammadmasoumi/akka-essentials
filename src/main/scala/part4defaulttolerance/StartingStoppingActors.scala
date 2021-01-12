package part4defaulttolerance

import akka.actor.{Actor, ActorSystem}

class StartingStoppingActors extends App {

  val system = ActorSystem("StoppingActorDemo")


  class Parent extends Actor {
    override def receive: Receive = ???
  }

}
