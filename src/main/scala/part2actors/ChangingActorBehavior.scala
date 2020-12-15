package part2actors

import akka.actor.Actor

object ChangingActorBehavior extends App {


  class FussyKid extends Actor {
    override def receive: Receive = ???
  }

  // object companion class
  object Mom {

    case class Food(food: String)

  }

  class Mom extends Actor {
    override def receive: Receive = ???
  }


}
