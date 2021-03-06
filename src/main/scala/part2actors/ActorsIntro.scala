package part2actors

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

object ActorsIntro extends App {

  // part1 - actor systems
  /*
    - heavy data structure
    - once per app
    - handle multi-threading
    - naming restriction (no space, no underscore, no hyphen, just normal characters)
   */
  val actorSystem = ActorSystem("FirstActorSystem")

  println(actorSystem.name)

  // part2 - create actors
  /*
    - actors are uniquely identified(with in the actor system).
    - messages are synchronous
    - each actor may respond differently(unique way of communication)
    - actors are really encapsulated
    - it's own internal data
   */

  class WordCountActor extends Actor {
    var totalWords = 0 // internal data
    val DELIMITER = " "

    /**
     * the actor behavior is a partial function
     * type Receive = PartialFunction[Any, Unit]
     *
     * @return nothing
     */
    override def receive: Receive = {
      case message: String =>
        println(s"[word counter]: I have received: $message")
        totalWords += message.split(DELIMITER).length
      case msg: Any => println(s"[word counter]: I cannot understand ${msg.toString}")
    }
  }

  // part3 - instantiate our actor
  /*
    - you cannot instantiate an actor by NEW
      - ERROR: you cannot instantiate an actor explicitly using the constructor [new]
    - the type of instance would be ActorRef
    - the Actor class is encapsulated and you have to communicate via interface
      - ERROR: actor name [wordCounter] is not unique!
   */

  val wordCounter: ActorRef = actorSystem.actorOf(Props[WordCountActor], "wordCounter")
  val anotherWordCounter: ActorRef = actorSystem.actorOf(Props[WordCountActor], "anotherWordCounter")
  // part4 - communicate!
  /*
    - communicate via exclamation mark. also called `TELL`
    - ! is a function (we can use either dot notation or infix notation)
   */
  wordCounter ! "I am learning Akka and it's pretty damn cool!" // infix notation
  anotherWordCounter.!("I am learning Akka and it's really damn cool!") // dot notation
  // sending message here is asynchronous!


  // How to instantiate an actor with constructor parameters
  class Person(name: String) extends Actor {
    override def receive: Receive = {
      case "hi" => println(s"Hello, I am $name")
      case "bye" => println("GoodBye")
      case _ =>
    }
  }

  // instantiating with companion object is the best pattern!
  object Person {
    def props(name: String) = Props(new Person(name))
  }

  val personActor: ActorRef = actorSystem.actorOf(Person.props("Bob"))
  personActor ! "hi"
}
