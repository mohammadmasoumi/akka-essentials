package part2actors

import akka.actor.Actor

object ChangingActorBehavior extends App {

  // object companion class
  object FussyKid {
    case object KidAccept
    case object KidReject
  }

  class FussyKid extends Actor {
    override def receive: Receive = ???
  }

  // object companion class
  object Mom {

    case class Food(food: String)
    case class Ask(message: String) // questions like: do you want to play?

  }

  class Mom extends Actor {
    override def receive: Receive = ???
  }


}
