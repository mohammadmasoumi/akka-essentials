package part1recap

object GeneralRecap extends App {

  val aCondition: Boolean = false // no reassignment
  var  aVariable = 32
  aVariable += 1 // aVariable = 43

  // expressions
  val aConditionedVal = if (aCondition) 42 else 65

  // code blocks
  val aCodeBlock = {
    if (aCondition) 74
    54 // the value of code block is equal to the last expression. here is 54
  }




}
