package part2actors

import akka.actor.{Actor, ActorSystem, Props}

object ActorCapabilities extends App {

  class SimpleActor extends Actor {
    override def receive: Receive = {
      case message: String => println(s"[simple actor] I have received `$message`")
      case number: Int => println(s"[simple actor] I have received a NUMBER: `$number`")
      case specialMsg: SpecialMessage => println(s"[simple actor] I have received a SpecialMessage: `$specialMsg`")
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  simpleActor ! "Hello, actor"

  // 1. messages can be of any type
  simpleActor ! 42

  case class SpecialMessage(contents: String)
  simpleActor ! SpecialMessage("some special contents!")





}
