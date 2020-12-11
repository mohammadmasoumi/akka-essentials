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
      case message: String => totalWords += message.split(DELIMITER).length
      case msg: Any => println(s"[word counter] I cannot understand ${msg.toString}")
    }
  }

  // part3 - instantiate our actor
  /*
    - you cannot instantiate an actor by NEW
    - the type of instance would be ActorRef
    - the Actor class is encapsulated and you have to communicate via interface
   */

  val wordCounter: ActorRef = actorSystem.actorOf(Props[WordCountActor], "wordCounter")

  // part4 - communicate!
  /*
    - communicate via exclamation mark
    - ! is a function (we can use either dot notation or infix notation)
   */
  wordCounter ! "I am learning Akka and it's pretty damn cool!" // infix notation
  wordCounter.!("I am learning Akka and it's pretty damn cool!") // dot notation


}
