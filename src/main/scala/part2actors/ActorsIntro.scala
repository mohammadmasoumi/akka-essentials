package part2actors

import akka.actor.{Actor, ActorSystem}

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
    }

  }


}
