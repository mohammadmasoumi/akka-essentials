package part5infra

import akka.actor.{Actor, ActorLogging}

object Routers extends App {

  /**
   * #1 = manual router
   */
  class Mater extends Actor with ActorLogging {
    private val slaves = for (_ <- 1 to 4) {

    }

    override def receive: Receive = {
      ???
    }
  }

  class Slave extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

}
