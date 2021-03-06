package part1recap

import scala.concurrent.Future

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

  // types aliases
  type ReceiveFunction = PartialFunction[Any, Unit]

  def receive: ReceiveFunction = {
    case _: Int => println("I've got an Int")
    case _: String => println("I've got a String")
    case _ => println("Bye!")
  }

  receive(2)

  // implicits
  implicit val timeout: Int = 3000

  def setTimeout(f: Int => Int)(implicit timeout: Int): Int = f(timeout)

  println(setTimeout(x => x + 1))

  // implicit conversion
  // 1. implicit defs
  case class Person(name: String) {
    def great = s"Hi, my name is $name"
  }

  implicit def fromStringToPerson(string: String): Person = Person(string)

  println("Bob".great)

  // this is equivalent to fromStringToPerson("Bob").great - automatically done by compiler!

  // 2. implicit classes
  implicit class Dog(name: String) {
    def bark: Unit = println("bark!")
  }

  "k9".bark
  // under the hood: new Dog("k9").bark

  // Organize - implicit scope
  /*
    1. local scope
    2. import scope
    3. companion object
   */

  // local scope
  implicit val inverseOrdering: Ordering[Int] = Ordering.fromLessThan(_ > _)
  println(List(1, 2, 3, 4).sorted)

  // imported scope
  // import an implicit execution context

  import scala.concurrent.ExecutionContext.Implicits.global

  val future = Future {
    println("Hello, future!")
  } // no implicit found for this method
  println(future)

  // companion objection
  object Person {
    implicit val personOrdering: Ordering[Person] = Ordering.fromLessThan((a, b) => a.name.compareTo(b.name) < 0)
  }

  println(List(Person("Bob"), Person("Alex")).sorted)

}
