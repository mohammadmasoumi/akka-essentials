package part1recap

object MultiThreadingRecap extends App {

  val aThread = new Thread(new Runnable {
    override def run(): Unit = println("I'm running in parallel!")
  })
  // syntactic sugar - more practical
  val aSyntacticSugarThread = new Thread(() => println("I'm running in parallel!"))

  aThread.start()
  aSyntacticSugarThread.start()


  val threadHello = new Thread(() => (1 to 100).foreach(_ => println("Hello")))
  val threadGoodbye = new Thread(() => (1 to 100).foreach(_ => println("Goodbye")))
}
