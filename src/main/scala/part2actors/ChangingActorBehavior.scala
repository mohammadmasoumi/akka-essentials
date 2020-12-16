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

    /*
    How it works

    - Food(veg) => stack.push(sadReceive)
    - Food(Chocolate) => stack.push(happyReceive)

    Stack:
      1. happyReceive
      2. sadReceive
      3. happyReceive

    * with context become

    - Food(veg) => stack.push(sadReceive)
    - Food(veg) => stack.push(sadReceive)
    - Food(Chocolate) => stack.push(happyReceive)

    Stack:
      1. sadReceive (popped)
      2. sadReceive
      3. happyReceive
     */

    def happyReceive: Receive = {
      case Food(VEGETABLE) =>
        // change my receive handler to SadReceive
        context.become(sadReceive, discardOld = false)
      case Food(CHOCOLATE) =>
      case Ask(message) =>
        println(s"${sender()} asked: $message")
        println(s"$name replied: Yes")
        sender() ! KidAccept
    }

    def sadReceive: Receive = {
      case Food(VEGETABLE) =>
        context.become(sadReceive, discardOld = false)
      case Food(CHOCOLATE) =>
        // change my receive handler to HappyReceive
        // context.become(happyReceive, false)
        context.unbecome()
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
        kid ! Food(VEGETABLE)
        kid ! Food(CHOCOLATE)
        kid ! Food(CHOCOLATE) // latest: kis is happy!
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

  /**
   * Exercise 1 - recreate the Counter Actor with context.become and NO MUTABLE STATE.
   */

  // companion actor object - COUNTER DOMAIN
  object Counter {

    case object CounterIncrement

    case object CounterDecrement

    case object CounterPrint

  }

  class Counter extends Actor {

    var count = 0

    override def receive: Receive = ???
  }

  import Counter._

  val counter = system.actorOf(Props[Counter], "myCounter")

  (1 to 5).foreach(_ => counter ! CounterIncrement)
  (1 to 5).foreach(_ => counter ! CounterDecrement)
  counter ! CounterPrint

  /**
   * Exercise 2 - a simplified voting system.
   */
  case class Vote(candidate: String)

  class Citizen extends Actor {
    override def receive: Receive = ???
  }

  class VoteAggregator extends Actor {
    override def receive: Receive = ???
  }

}
