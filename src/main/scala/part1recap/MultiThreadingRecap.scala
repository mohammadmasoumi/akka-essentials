package part1recap

import akka.actor.Status.Success

import scala.concurrent.Future
import scala.util

object MultiThreadingRecap extends App {

  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("I'm running in parallel!")
  })
  // syntactic sugar - more practical
  val aSyntacticSugarThread = new Thread(() => println("I'm running in parallel!"))

  aThread.start()
  aSyntacticSugarThread.start()

  // threading
  val threadHello = new Thread(() => (1 to 100).foreach(_ => println("Hello")))
  val threadGoodbye = new Thread(() => (1 to 100).foreach(_ => println("Goodbye")))

  // different ordering - different runs produce different results!
  threadHello.start()
  threadGoodbye.start()

  /**
   *
   * @param amount            : This variable is thread safe in synchronized block
   * @param aThreadSafeAmount : This variable is thread safe because of volatile annotation
   */
  class BankAccount(private var amount: Int = 0, @volatile private var aThreadSafeAmount: Int = 0) {
    override def toString: String = "" + amount

    def withdraw(money: Int): Unit = amount -= money // not thread safe

    def safeWithdraw(money: Int): Unit = this.synchronized {
      amount -= money
    }

    def deposit(money: Int): Unit = amount += money // not thread safe

    def safeDeposit(money: Int): Unit = this.synchronized {
      amount += money
    }
  }

  // inter-thread communication on the JVm
  // wait - notify mechanism

  // Scala Futures

  import scala.concurrent.ExecutionContext.Implicits.global

  val future = Future {
    // long computation - on a different thread
    42
  }

  // callbacks
  future.onComplete {
    case util.Success(42) => println("I found the meaning of the life!")
  }


}
