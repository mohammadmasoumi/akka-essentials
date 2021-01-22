package part5infra

import akka.actor.{Actor, ActorLogging, Props}

object Routers extends App {

  /**
   * #1 = manual router
   */
  class Mater extends Actor with ActorLogging {
    private val slaves = for (_ <- 1 to 5) yield {
      val slave = context.actorOf(Props[Slave])
      context.watch(slave)
      slave // TODO
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
