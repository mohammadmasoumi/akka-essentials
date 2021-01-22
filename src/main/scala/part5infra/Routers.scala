package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

object Routers extends App {

  /**
   * #1 = manual router
   */
  class Master extends Actor with ActorLogging {
    // STEP 1 - create routees
    // 5 actor routees base off the Slave actors
    private val slaves = for (idx <- 1 to 5) yield {
      val slave = context.actorOf(Props[Slave], s"slave_$idx")
      context.watch(slave)
      ActorRefRoutee(slave) // TODO
    }

    // STEP 2 - define router
    /**
     * Routing logic
     * 1- routing strategy
     * 2- actor
     *
     * Strategies:
     *  - round-robin
     *  - random
     *  - smallest mailbox
     *  - broadcast
     *  - scatter-gather-first
     *  - tail-chopping
     *  - consistent-hashing
     */
    private val router = Router(RoundRobinRoutingLogic(), slaves)

    override def receive: Receive = onMessage(router)

    private def onMessage(router: Router): Receive = {
      // STEP 4 - handle the termination/lifecycle of the routees
      case Terminated(ref) =>
        // remove slave from the router
        context.become(onMessage(router.removeRoutee(ref)))
        // create new slave
        val newSlave = context.actorOf(Props[Slave])
        context.watch(newSlave)
        // add new slave to the router
        context.become(onMessage(router.addRoutee(newSlave)))

      // STEP 3 - route the messages
      case message =>
        router.route(message, sender())
    }
  }

  class Slave extends Actor with ActorLogging {
    override def receive: Receive = {
      case message => log.info(message.toString)
    }
  }

  val system = ActorSystem("RoutersDemo" /* TODO add some config here*/)
  val master = system.actorOf(Props[Master])

  for (idx <- 1 to 10) {
    master ! s"[$idx] Hello from the world!"
  }


}
