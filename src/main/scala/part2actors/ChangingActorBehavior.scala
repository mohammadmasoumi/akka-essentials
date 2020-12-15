package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import part2actors.ChangingActorBehavior.Mom.MomStart

object ChangingActorBehavior extends App {

  // object companion class
  object FussyKid {
    val HAPPY = "happy"
    val SAD = "sad"

    case object KidAccept

    case object KidReject

    def props(name: String) = Props(new FussyKid(name))
  }

  class FussyKid(name: String) extends Actor {

    import FussyKid._
    import Mom._

    // internal state of the kid
    var state = HAPPY

    override def receive: Receive = {
      case Food(VEGETABLE) =>
        state = SAD
        println(s"[$name]: I'm $state")
      case Food(CHOCOLATE) =>
        state = HAPPY
        println(s"[$name]: I'm $state")
      case Ask(_) =>
        if (state == HAPPY) sender() ! KidAccept
        else sender() ! KidReject
    }
  }

  // object companion class
  object Mom {
    val VEGETABLE = "veggies"
    val CHOCOLATE = "chocolate"

    case class MomStart(kidReference: ActorRef)

    case class Food(food: String)

    case class Ask(message: String) // questions like: do you want to play?

    def props(name: String) = Props(new Mom(name))
  }

  object StatelessFussyKid {

    def props(name: String) = Props(new StatelessFussyKid(name))
  }

  class StatelessFussyKid(name: String) extends Actor {

    import FussyKid._
    import Mom._

    override def receive: Receive = happyReceive // call no parameter method

    def happyReceive: Receive = {
      case Food(VEGETABLE) =>
        // change my receive handler to SadReceive
        context.become(sadReceive)
      case Food(CHOCOLATE) =>
      case Ask(message) =>
        println(s"${sender()} asked: $message")
        println(s"$name replied: Yes")
        sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) =>
      case Food(CHOCOLATE) =>
        // change my receive handler to HappyReceive
        context.become(happyReceive)
      case Ask(message) =>
        println(s"[${sender()}] asked: $message")
        println(s"$name replied: No")
        sender() ! KidReject
    }
  }

  class Mom(name: String) extends Actor {

    import FussyKid._
    import Mom._

    override def receive: Receive = {
      case MomStart(kid: ActorRef) =>
        // test our interaction
        kid ! Food(VEGETABLE)
        kid ! Ask(s"[$name]: do you want to play?")
      case KidAccept =>
        println(s"[$name]: Yeah, my kid is happy!")
      case KidReject =>
        println(s"[$name]: My kid is sad. but as he's healthy")
    }
  }

  val system = ActorSystem("ChangingActorBehaviorDemo")

  val bob = system.actorOf(FussyKid.props("bob"))
  val marry = system.actorOf(Mom.props("marry"))
  val david = system.actorOf(StatelessFussyKid.props("david"))

  marry ! MomStart(david)

}
