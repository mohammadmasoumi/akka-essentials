package part2actors

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}

object ChildActors extends App {

  // Actors can create other actors
  object Parent {
    case class CreateChild(name: String)
    case class TellChildren(message: String)
    def props(name: String): Props = Props(new Parent(name))
  }

  class Parent(parentName: String) extends Actor {

    import Parent._

    override def receive: Receive = parentHandler()

    def parentHandler(children: Set[ActorRef] = Set()): Receive = {
      case CreateChild(name) =>
        println(s"[$parentName]: ${self.path} creating child ...")
        // create a new actor right HERE
        val childRef = context.actorOf(Child.props(name), name)
        context.become(parentHandler(children + childRef))
      case TellChildren(message) =>
        children.foreach(child => child forward  message)
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
  import Parent._

  val system = ActorSystem("ParentChildDemo")
  val parent = system.actorOf(Parent.props("parent"))

  parent ! CreateChild("marry")
  parent ! CreateChild("john")
  parent ! CreateChild("daniel")
  parent ! TellChildren("mom and dad love you all.")

  // actor hierarchies
  // parent -> child1 -> grandchild
  //           child2 -> grandchild

  /*
    Does parent has it's own parent? No

    Guardian actors (top-level):
      - /system = system guardian
      - /user = user-level guardian(programmer guardian)
      - / = the root guardian(if this guy die the whole system will die)
   */

  /**
   * Actor selection
   */

  val childSelection: ActorSelection = system.actorSelection("/user/$a/daniel")
  childSelection ! "I found you baby"



}
