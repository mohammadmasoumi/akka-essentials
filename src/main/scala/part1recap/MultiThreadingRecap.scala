package part1recap

import scala.concurrent.Future
import scala.util.Failure

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
    case util.Success(42) => println("I found the meaning of life!")
    case Failure(_) => println("Something happened with the meaning of life!")
  }

  val aProcessedFuture = future.map(_ + 1) // future with 43
  val aFlatFuture = future.flatMap {
    value => Future(value + 2)
  } // Future with 44

  val filterFuture = future.filter(_ % 2 == 0) // if it fails it will fail with a NO SUCH ELEMENT

  // cause it supports filer, map and flatmap, it will support for-comprehension too
  val aNonSenseFuture = for {
    meaningOfLife <- future
    filteredMeaning <- filterFuture
  } yield meaningOfLife + filteredMeaning

  // andThen, recover/recoverWith
  // Promises
}
