package part2actors

import akka.actor.Actor

object ChangingActorBehavior extends App {

  // object companion class
  object FussyKid {

    case object KidAccept

    case object KidReject

    val HAPPY = "happy"
    val SAD = "sad"
  }

  class FussyKid extends Actor {

    import FussyKid._
    import Mom._

    // internal state of the kid
    var state = HAPPY

    override def receive: Receive = {
      case Food(VEGETABLE) => state = SAD
      case Food(CHOCOLATE) => state = HAPPY
      case Ask(_) =>
        if (state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }

  // object companion class
  object Mom {

    case class Food(food: String)

    case class Ask(message: String) // questions like: do you want to play?
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"
  }

  class Mom extends Actor {
    override def receive: Receive = ???
  }

}
