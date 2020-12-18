package part2actors

import akka.actor.Actor

object ChildActors extends App {

  // Actors can create other actors

  class Parent extends Actor {
    override def receive: Receive = ???
  }

}
