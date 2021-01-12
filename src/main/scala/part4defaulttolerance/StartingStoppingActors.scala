package part4defaulttolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}

object StartingStoppingActors extends App {

  val system = ActorSystem("StoppingActorDemo")


  class Parent extends Actor with ActorLogging {

    import Parent._

    override def receive: Receive = withChildren(Map())

    def withChildren(children: Map[String, ActorRef]): Receive = {
      case StartChild(name) =>
        log.info(s"Starting child $name")
        context.become(withChildren(children + (name -> context.actorOf(Props[Child], name))))
      case StopChild(name) =>
        log.info(s"Stopping child with name $name")
        val childOption = children.get(name)

        /**
         * context.stop() is a non-blocking method! asynchronously!
         */
        childOption.foreach(child => context.stop(child))

      case Stop =>
        log.info("Stopping myself!")

        /**
         * Asynchronous
         * Also stops all its children!
         */
        context.stop(self)
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

  // testing scenarios

  import Parent._

  val parent = system.actorOf(Props[Parent], "parent")
  parent ! StartChild("child1")

  val child = system.actorSelection("/user/parent/child1")
  child ! "Hi kid!"

  parent ! StopChild("child1")
  for (_ <- 1 to 50) child ! "Are you there?"

  // creating another child
  parent ! StartChild("child2")
  val child2 = system.actorSelection("/user/parent/child2 ")
  child2 ! "hi, seocnd child"

  // parent stops himself and its children
  parent ! Stop
  for (_ <- 1 to 10) parent ! "parent, Are you still there?"
  for (I <- 1 to 100) child2 ! s"[$I]: second kid, Are you still alive?"


}
