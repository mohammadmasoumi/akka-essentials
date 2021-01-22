package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}

object Routers extends App {

  /**
   * #1 = manual router
   */
  class Mater extends Actor with ActorLogging {
    // STEP 1 - create routees
    // 5 actor routees base off the Slave actors
    private val slaves = for (_ <- 1 to 5) yield {
      val slave = context.actorOf(Props[Slave])
      context.watch(slave)
      ActorRefRoutee(slave) // TODO
    }

    // STEP 2 - define router
    /**
     * Routing logic
     * 1- routing strategy
     * 2- actor
     */
    private def createDefaultRouter(): Router =
      Router(RoundRobinRoutingLogic(), slaves)

    override def receive: Receive = routeMessages()

    def routeMessages(router: Router = createDefaultRouter()): Receive = {
      // STEP 4 - handle the termination/lifecycle of the routees
      case Terminated(ref) =>
        // remove slave from the router
        var newRouter = router.removeRoutee(ref)
        // create new slave
        val newSlave = context.actorOf(Props[Slave])
        context.watch(newSlave)
        // add new slave to the router
        newRouter = router.addRoutee(newSlave)
        context.become(routeMessages(newRouter))

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


}
