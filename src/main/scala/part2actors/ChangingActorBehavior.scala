package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}


object ChangingActorBehavior extends App {

  // object companion class
  object FussyKid {
    val HAPPY = "happy"
    val SAD = "sad"

    case object KidAccept

    case object KidReject

    def props(name: String): Props = Props(new FussyKid(name))
  }

  class FussyKid(name: String) extends Actor {

    import FussyKid._
    import Mom._

    // internal state of the kid
    var state: String = HAPPY

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

    def props(name: String): Props = Props(new Mom(name))
  }

  object StatelessFussyKid {

    def props(name: String): Props = Props(new StatelessFussyKid(name))
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

  val tom = system.actorOf(FussyKid.props("tom"))
  val marry = system.actorOf(Mom.props("marry"))
  val david = system.actorOf(StatelessFussyKid.props("david"))

  // marry ! MomStart(david)

  // -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------
  // ------------------------------------------ Assignments ------------------------------------------
  // -------------------------------------------------------------------------------------------------
  // -------------------------------------------------------------------------------------------------

  /**
   * Exercise 1 - recreate the Counter Actor with context.become and NO MUTABLE STATE.
   */

  // companion actor object - COUNTER DOMAIN
  object Counter {

    case object Increment

    case object Decrement

    case object Print

  }

  /**
   * stateless actor
   * state => receiver input arguments
   */
  class Counter extends Actor {

    import Counter._

    override def receive: Receive = countReceive(0)

    def countReceive(currentCount: Int): Receive = {
      case Increment =>
        println(s"[Counter]: current count is: $currentCount | Incrementing ...")
        context.become(countReceive(currentCount + 1))
      case Decrement =>
        println(s"[Counter]: current count is: $currentCount | Decrementing ...")
        context.become(countReceive(currentCount - 1))
      case Print => println(s"[Counter]: current count is: $currentCount")
    }

  }

  import Counter._

  val counter = system.actorOf(Props[Counter], "myCounter")

  (1 to 5).foreach(_ => counter ! Increment)
  (1 to 5).foreach(_ => counter ! Decrement)
  counter ! Print

  /**
   * Exercise 2 - a simplified voting system.
   */
  case class Vote(candidate: String)

  case object VoteStatusRequest

  case class VoteStatusReply(candidate: Option[String])

  class Citizen extends Actor {
    override def receive: Receive = ??? // TODO
  }

  case class AggregateVotes(citizens: Set[ActorRef])

  class VoteAggregator extends Actor {
    override def receive: Receive = ??? // TODO
  }

  //  val alice = system.actorOf(Props[Citizen])
  //  val bob = system.actorOf(Props[Citizen])
  //  val charlie = system.actorOf(Props[Citizen])
  //  val daniel = system.actorOf(Props[Citizen])
  //
  //  alice ! Vote("Martin")
  //  bob ! Vote("Jonas")
  //  charlie ! Vote("Roland")
  //  daniel ! Vote("Roland")
  //
  //  val voteAggregator = system.actorOf(Props[VoteAggregator])
  //  voteAggregator ! AggregateVotes(Set(alice, bob, charlie, daniel))

  /*
    print the status of the votes
      Martin -> 1
      Jonas -> 1
      Roland -> 2
   */

}
