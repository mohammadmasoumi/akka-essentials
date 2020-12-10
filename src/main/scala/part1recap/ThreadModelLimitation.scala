package part1recap

object ThreadModelLimitation extends App {

  /**
   * OOP encapsulation is only valid in the SINGLE THREADED MODEL.
   */

  class BankAccount(private var amount: Int) {
    override def toString: String = "" + amount

    def withdraw(money: Int): Unit = amount -= money // not thread safe
    def deposit(money: Int): Unit = amount += money // not thread safe
    def getAmount: Int = amount
  }

  val account = new BankAccount(2000)
  for (_ <- 1 to 100) {
    new Thread(() => account.withdraw(1)).start()
  }
  for (_ <- 1 to 100) {
    new Thread(() => account.deposit(1)).start()
  }
  println(account.getAmount) // OOP encapsulation is broken in the multi-threaded env

  // Synchronization! | Locks to rescue
  // deadlocks, live-locks

  /*
    What we need?
      - fully encapsulated
      - with no locks
   */

  /**
   * delegating something to a thread is a pain
   */
  // executor service
  // you have a running thread and you want to pass a runnable to that thread

  var task: Runnable = null

  // consumer
  val runningThread: Thread = new Thread(() => {
    while (true) {
      while (task == null) {
        runningThread.synchronized {
          println("[background] waiting for a task ...")
          runningThread.wait()
        }
      }
      task.synchronized {
        println("[background] I have a task!")
        task.run()
        task = null
      }
    }
  })

  // producer
  def delegateToBackgroundThread(aRunnable: Runnable): Unit = {
    if (task == null) task = aRunnable

    runningThread.synchronized {
      println("[background] notifying running thread!")
      runningThread.notify()
    }
  }

  runningThread.start()
  Thread.sleep(1000)
  delegateToBackgroundThread(() => println("Hello"))
  Thread.sleep(1000)
  delegateToBackgroundThread(() => println("GoodBye"))

  /*
    What we need?
      - can safely receive messages
      - can identify the sender
      - is easily identifiable
      - can guard against errors
   */


}
