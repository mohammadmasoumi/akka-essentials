package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorCapabilities extends App {

  class SimpleActor extends Actor {
    // actor context
    // context.system
    // context.self -> actor reference === this(OOP)
    // we can also send message to ourselves! pretty damn cool!
    // context.self === self

    override def receive: Receive = {
      case "Hi!" => context.sender() ! "Hello, there!" // context.sender(): ActorRef | the last sender ref
      case message: String => println(s"[${context.self.path}] I have received `$message`")
      case number: Int => println(s"[${context.self}] I have received a NUMBER: `$number`")
      case SpecialMessage(contents) => println(s"[${self}] I have received sth SPECIAL: `$contents`")
      case SendMessageToYourself(content) => self ! content // not practical
      case SayHiTo(ref) => ref ! "Hi!" // equivalent to (ref ! "Hi!")(self) | self === implicit value
      case WirelessPhoneMessage(content, ref) => ref forward (content + "s") // I keep the original sender
    }
  }

  val system = ActorSystem("actorCapabilitiesDemo")
  val simpleActor = system.actorOf(Props[SimpleActor], "simpleActor")

  // simpleActor ! "Hello, actor"

  // 1. messages can be of any type
  /*
      a) messages must be IMMUTABLE
      b) messages must be SERIALIZABLE - JVm can cast it to BYTE-STREAM and send it to another JVM
   */

  // in practice: use case classes and case objects
  // simpleActor ! 42 // [GOOD QUESTION] Who is the sender? null

  // any type
  case class SpecialMessage(contents: String)

  // simpleActor ! SpecialMessage("some special contents!")

  // 2. actors have information about their context and about themselves
  // context.self === `this` in OOP

  case class SendMessageToYourself(content: String)

  // simpleActor ! SendMessageToYourself("I am an actor and I am proud of it")

  // 3. actors can REPLY to messages
  val alice = system.actorOf(Props[SimpleActor], "alice")
  val bob = system.actorOf(Props[SimpleActor], "bob")

  case class SayHiTo(ref: ActorRef) // ActorRef: which actor to send messages to
  // alice ! SayHiTo(bob)

  // 4. dead letters
  /*
    [dead-letter]: garbage pool
    dead letter handle not delivered messages
    IF THERE IS NO SENDER => reply go to dead letter
   */
  // alice ! "Hi!" // reply to "me"

  // 5. forwarding messages
  // D -> A -> B
  // forwarding
  case class WirelessPhoneMessage(content: String, ref: ActorRef)

  // alice ! WirelessPhoneMessage("Hi", bob) // the original sender is no sender[dead letters]

  /**
   * Exercise
   * 1. a Counter actor
   *  - Increment
   *  - Decrement
   *  - Print
   *
   * 2. a Bank account as an actor
   * receive
   *  - Deposit an account
   *  - Withdraw an account
   *  - Statement
   *    replies with
   *  - success
   *  - failure
   *
   * hint; interact with some other kind of actor
   */

  case class Increment(count: Int)

  case class Decrement(count: Int)

  class aCounterActor(var initialNumber: Int) extends Actor {
    override def receive: Receive = {
      case Increment(number: Int) =>
        initialNumber += number
        println(s"Incremented by $number | current: $initialNumber")
      case Decrement(number: Int) =>
        initialNumber -= number
        println(s"Decremented by $number | current: $initialNumber")
      case "print" => println(s"my current number is: $initialNumber")
    }
  }

  object aCounterActor {
    def props(initialNumber: Int = 0) = Props(new aCounterActor(initialNumber))
  }

  val aCounter = system.actorOf(aCounterActor.props(), "aCounter")

  aCounter ! Increment(10)
  aCounter ! Decrement(10)
  aCounter ! "print"

  // -- Daniel solution

  // companion actor object - COUNTER DOMAIN
  object Counter {

    case object CounterIncrement

    case object CounterDecrement

    case object CounterPrint

  }

  class Counter extends Actor {

    import Counter._

    var count = 0

    override def receive: Receive = {
      case CounterIncrement => count += 1
      case CounterDecrement => count += 1
      case CounterPrint => println(count)
    }
  }

  import Counter._

  val counter = system.actorOf(Props[Counter], "myCounter")
  (1 to 5).foreach(_ => counter ! CounterIncrement)
  (1 to 5).foreach(_ => counter ! CounterDecrement)
  counter ! CounterPrint


  // bank account


  object BankAccount {

    case class Deposit(amount: Int)

    case class Withdraw(amount: Int)

    case object Statement

    case class TransactionSuccess(message: String)

    case class TransactionFailure(message: String)

  }

  class BankAccount extends Actor {

    import BankAccount._

    var funds = 0

    override def receive: Receive = {
      case Deposit(amount) =>
        if (amount < 0)
      case Withdraw(amount) =>
      case Statement =>
    }
  }

}
