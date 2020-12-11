package part2actors

import akka.actor.ActorSystem

object ActorsIntro extends App {

  // part1 - actor systems
  val actorSystem = ActorSystem("FirstActorSystem")

  println(actorSystem.name)


}
