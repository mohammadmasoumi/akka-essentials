package part1recap

object AdvancedRecap extends App {


  // partial function
  val partialFunction: PartialFunction[Int, Int] = {
    case 1 => 42
    case 2 => 43
    case 3 => 44
  }
  // validate domain for this partialFunction (42, 43, 44)
  // anything else will throw an exception

  val pf = (x: Int) => x match {
    case 1 => 42
    case 2 => 43
    case 3 => 44
  } // partial functions are powered by pattern matching

  val aFunction: (Int => Int) = partialFunction
  try {
    println(aFunction(1))
    println(aFunction(42)) // out of partial function domain
  } catch {
    case e: MatchError => println(s"Caught a MatchError: $e")
  }

  val modifiedList = List(1, 2, 3, 4).map {
    case 1 => 42
    case _ => 0
  } // partial function
  println(modifiedList)

  // partial function features

  // lifting
  val lifted = partialFunction.lift // total function Int => Option[Int]
  println(lifted(2)) // Some(43)
  println(lifted(5000)) // None

  // orElse
  val pfChain = partialFunction.orElse[Int, Int] {
    case 4 => 45
  }
  try {
    println(pfChain(3))
    println(pfChain(4))
    println(pfChain(5)) // throw a match error
  } catch {
    case e: MatchError => s"Caught a MatchError: $e"
  }

  // types
  type ReceiveFunction = PartialFunction[Any, Unit]

  def receive: ReceiveFunction = {
    case _: Int => println("I've got an Int")
    case _: String => println("I've got a String")
    case _ => println("Bye!")
  }
  receive(2)


}
