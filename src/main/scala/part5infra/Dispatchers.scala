package part5infra

import akka.actor.{Actor, ActorLogging}

object Dispatchers extends App {

  class Counter extends Actor with ActorLogging {
    override def receive: Receive = ???
  }

}

