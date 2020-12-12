package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorCapabilities extends App {

  class SimpleActor extends Actor {
    // actor context
    // context.system
    // context.self -> actor reference == this(OOP)

    override def receive: Receive = {
      case message: String => println(s"[${context.self}] I have received `$message`")
      case number: Int => println(s"[${context.self}] I have received a NUMBER: `$number`")
      case SpecialMessage(contents) => println(s"[${context.self}] I have received sth SPECIAL: `$contents`")
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "Hello, actor"

  // 1. messages can be of any type
  /*
      a) messages must be IMMUTABLE
      b) messages must be SERIALIZABLE - JVm can cast it to BYTE-STREAM and send it to another JVM
   */

  // in practice: use case classes and case objects
  simpleActor ! 42

  // any type
  case class SpecialMessage(contents: String)

  simpleActor ! SpecialMessage("some special contents!")

  // 2. actors have information about their context and about themselves


}
