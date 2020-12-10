package part1recap

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

  class BankAccount(private var amount: Int = 0) {
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


}
