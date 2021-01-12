package part4defaulttolerance

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, PoisonPill, Props}

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
         *
         * first, stops all its children.
         * then, stops himself as well.
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


  /**
   * method #1 - using context.stop
   */

  //  val parent = system.actorOf(Props[Parent], "parent")
  //  parent ! StartChild("child1")
  //
  //  val child1 = system.actorSelection("/user/parent/child1")
  //  child1 ! "Hi kid!"
  //
  //  parent ! StopChild("child1")
  //  for (i <- 1 to 50) child1 ! "[FIRST CHILD $i]: Are you there?"
  //
  //  // creating another child
  //  parent ! StartChild("child2")
  //  val child2 = system.actorSelection("/user/parent/child2 ")
  //  child2 ! "hi, second child"
  //
  //  // parent stops himself and its children
  //  //  parent ! Stop
  //  for (i <- 1 to 10) parent ! "[PARENT $i]: Are you still there?"
  //  for (i <- 1 to 100) child2 ! s"[SECOND CHILD $i]: second kid, Are you still alive?"

  /**
   * method #2 - using special messages
   */

  val looseActor = system.actorOf(Props[Child])
  looseActor ! "Hello, loose actor"
  looseActor ! PoisonPill // so funny :) | commiting suicide
  looseActor ! "Are you there?" // catch by dead letter

}
