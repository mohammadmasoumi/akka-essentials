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



}
