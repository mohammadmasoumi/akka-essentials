package part1recap

import scala.annotation.tailrec

object GeneralRecap extends App {

  val aCondition: Boolean = false // no reassignment
  var aVariable = 32
  aVariable += 1 // aVariable = 43

  // expressions
  val aConditionedVal = if (aCondition) 42 else 65

  // code blocks
  val aCodeBlock = {
    if (aCondition) 74
    54 // the value of code block is equal to the last expression. here is 54
  }

  // types
  // Unit
  val aUnit: Unit = println("Hello, scala") // side affect

  // functions
  def aFunction(x: Int): Int = x + 1

  // recursion - TAIL recursion
  @tailrec
  def factorial(num: Int, acc: Int = 1): Int = {
    if (num <= 1) acc
    else factorial(num - 1, num * acc)
  }
  println(factorial((5)))

}
