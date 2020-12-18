package part2actors

import akka.actor.Actor

object ChildActors extends App {

  // Actors can create other actors
  object Parent {
    case class CreateChild(name: String)
    case class TellChild(name: String)
  }

  class Parent extends Actor {
    import Parent._
    override def receive: Receive = ???
  }

  class Child extends Actor {
    override def receive: Receive = {
      case message: String => println(s"${self.path} I got: $message")
    }
  }

}
