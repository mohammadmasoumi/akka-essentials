package part2actors

import akka.actor.{Actor, ActorRef, ActorSelection, ActorSystem, Props}
import part2actors.ChildActors.CreditCard.AttachToAccount

object ChildActors extends App {

  // Actors can create other actors
  object Parent {
    case class CreateChild(name: String)
    case class TellChildren(message: String)
    def props(name: String): Props = Props(new Parent(name))
  }

  class Parent(parentName: String) extends Actor {

    import Parent._

    override def receive: Receive = parentHandler()

    def parentHandler(children: Set[ActorRef] = Set()): Receive = {
      case CreateChild(name) =>
        println(s"[$parentName]: ${self.path} creating child ...")
        // create a new actor right HERE
        val childRef = context.actorOf(Child.props(name), name)
        context.become(parentHandler(children + childRef))
      case TellChildren(message) =>
        children.foreach(child => child forward  message)
    }
  }

  object Child {
    def props(name: String): Props = Props(new Child(name))
  }

  class Child(name: String) extends Actor {
    override def receive: Receive = {
      case message: String => println(s"[$name]: ${self.path} I got: $message")
    }
  }
  import Parent._

  val system = ActorSystem("ParentChildDemo")
  val parent = system.actorOf(Parent.props("parent"))

  parent ! CreateChild("marry")
  parent ! CreateChild("john")
  parent ! CreateChild("daniel")
  parent ! TellChildren("mom and dad love you all.")

  // actor hierarchies
  // parent -> child1 -> grandchild
  //           child2 -> grandchild

  /*
    Does parent has it's own parent? No

    Guardian actors (top-level):
      - /system = system guardian
      - /user = user-level guardian(programmer guardian)
      - / = the root guardian(if this guy die the whole system will die)
   */

  /**
   * Actor selection
   */

  val childSelection: ActorSelection = system.actorSelection("/user/$a/daniel")
  childSelection ! "I found you baby"

  val childSelectionWithInvalidPath: ActorSelection = system.actorSelection("/user/$a/daniel")
  childSelectionWithInvalidPath ! "I found you baby" // this message will be sent to dead letter

  /**
   * Danger!
   *
   * NEVER PASS MUTABLE ACTOR STATE, OR THE `THIS` REFERENCE, TO CHILD ACTORS.
   *
   * NEVER IN YOUR LIFE
   */

  object NaiveBankAccount {
    case class Deposit(amount: Int)
    case class Withdraw(amount: Int)
    case object InitializeAccount
  }

  class NaiveBankAccount extends Actor {
    import NaiveBankAccount._
    import CreditCard._

    var amount = 0
    override def receive: Receive = {
      case InitializeAccount =>
        val creditCardRed = context.actorOf(Props[CreditCard])
        creditCardRed ! AttachToAccount(this) // !!
      case Deposit(funds) => deposit(funds)
      case Withdraw(funds) => withdraw(funds)
    }

    def deposit(funds: Int): Unit = amount += funds
    def withdraw(funds: Int): Unit = amount -= funds
  }

  object CreditCard {
    case class AttachToAccount(bankAccount: NaiveBankAccount) // !!
    case object CheckStatus
  }
  class CreditCard extends Actor {
    import CreditCard._

    override def receive: Receive = {
      case AttachToAccount(account) => context.become(attachedTo(account))
      case CheckStatus =>
    }
    def attachedTo(account: NaiveBankAccount): Receive = {
      case _ => 
    }
  }

}
