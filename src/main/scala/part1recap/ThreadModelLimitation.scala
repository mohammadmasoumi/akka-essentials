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
  println(account.getAmount)




}
