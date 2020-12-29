package part2actors

import akka.actor.Actor

object ChildActorExercise extends App {

  // distributed word counting
  object WordCounterMaster {

    case class Initialize(nChildren: Int)

  }

  class WordCounterMaster extends Actor {
    override def receive: Receive = ???
  }

  class WordCounterWorker extends Actor {
    override def receive: Receive = ???
  }

}
