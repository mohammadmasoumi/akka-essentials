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

  // typesI
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

  // OOP
  class Animal

  class Dog extends Animal

  val aDog: Animal = new Dog

  trait Carnivore {
    def eat(a: Animal): Unit
  }

  class Crocodile extends Animal with Carnivore {
    override def eat(a: Animal): Unit = println("NamNamNam!")
  }

  // method notations
  val aCroc = new Crocodile
  aCroc.eat(aDog)
  aCroc eat aDog

  // anonymous classes
  val aCarnivore = new Carnivore {
    override def eat(a: Animal): Unit = println("I eat meat!")
  }
  aCarnivore eat aDog

  // generics
  abstract class MyList[+A]

  // companion objects
  object MyList

  // case classes
  case class Person(name: String, age: Int) // a LOT in this course

  // Exception
  val aPotentialFailure: Unit = try {
    throw new RuntimeException("I'm innocent, I swear!") // return Nothing
  } catch {
    case e: Exception => println("I caught an exception!")
  } finally {
    // side effects
    println("some logs")
  }

  // Functional programming
  val incrementer = new Function[Int, Int] {
    override def apply(v1: Int): Int = v1 + 1
  }
  val incremented = incrementer(42) // 43
  // incrementer.apply(42)
  println(incremented)

  val anonymousIncrementer = (x: Int) => x + 1
  // Int => Int === Function1[Int, Int]

  // FP is all about working with functions as first class
  List(1, 2, 3).map(incrementer)
  // HOF => High Order Function

  // for comprehensions
  val pairs = for {
    num <- List(1, 2, 3, 4)
    char <- List('a', 'b', 'c', 'd')
  } yield num + "-" + char
  // List(1, 2, 3, 4).flatMap(num => List('a', 'b', 'c', 'd').map(char => num + "-" + char))

  // Seq, Array, List, Vector, Map, Tuples, Sets

  // "Collections"
  // Option and Try
  val anOption = Some(2)
  val aTry = {
    throw new RuntimeException
  }

  // pattern matching
  val unknown = 2
  val order = unknown match {
    case 1 => "first"
    case 2 => "second"
    case 3 => "third"
    case _ => "unknown"
  }

  val bob = Person("Bob", 22)
  val greeting = bob match {
    case Person(n, _) => s"Hi, my name is $n"
    case _ => "I don't know my name!"
  }

}
