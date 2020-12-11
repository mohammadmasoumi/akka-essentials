package part2actors

import akka.actor.ActorSystem

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




}
