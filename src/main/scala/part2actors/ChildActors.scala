package part2actors

import akka.actor.{Actor, ActorRef, Props}

object ChildActors extends App {

  // Actors can create other actors
  object Parent {
    case class CreateChild(name: String)
    case class TellChildren(message: String)
  }

  class Parent extends Actor {

    import Parent._

    override def receive: Receive = parentHandler()

    def parentHandler(children: Set[ActorRef] = Set()): Receive = {
      case CreateChild(name) =>
        println(s"${self.path} creating child ...")
        // create a new actor right HERE
        val childRef = context.actorOf(Child.props(name), name)
        context.become(parentHandler(children + childRef))
      case TellChildren(message) =>
        children.foreach(child => child ! message)
    }
  }

  object Child {
    def props(name: String): Props = Props(new Child(name))
  }

  class Child(name: String) extends Actor {
    override def receive: Receive = {
      case message: String => println(s"[$name]: ${self.path} I got: $message")
    }
  }

}
