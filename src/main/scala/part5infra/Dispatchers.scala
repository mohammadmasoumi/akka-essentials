package part5infra

import akka.actor.{Actor, ActorLogging}

object Dispatchers extends App {

  class Counter extends Actor with ActorLogging {
    override def receive: Receive = onMessage()

    private def onMessage(count: Int = 0): Receive = {
      case message =>
        context.become(onMessage(count + 1))
        log.info(s"[$count]: $message")
    }
  }

}

