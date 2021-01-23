package part5infra

import akka.actor.{Actor, ActorLogging, ActorSystem, Props, Terminated}
import akka.routing._
import com.typesafe.config.ConfigFactory

object Routers extends App {

  /**
   * Method #1 = manual router
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

  val system = ActorSystem("RoutersDemo", ConfigFactory.load().getConfig("routersDemo"))
  val master = system.actorOf(Props[Master])

  //  for (idx <- 1 to 10) {
  //    master ! s"[$idx] Hello from the world!"
  //  }

  /**
   * Method #2 = a router actor with its own children
   * POOL ROUTER
   */

  // 2.1 programmatically (in code)
  val poolMaster = system.actorOf(RoundRobinPool(5).props(Props[Slave]), "simplePoolMaster")
  //  for (idx <- 1 to 10) {
  //    poolMaster ! s"[$idx] Hello from the world!"
  //  }

  // 2.2 from configuration
  val poolMaster2 = system.actorOf(FromConfig.props(Props[Slave]), "poolMaster2")
  //    for (idx <- 1 to 10) {
  //      poolMaster2 ! s"[$idx] Hello from the world!"
  //    }

  /**
   * Method #3 = a router with actors created elsewhere
   * GROUP router
   */

  // .. in another part of my application
  val slaveList = (1 to 5).map(idx => system.actorOf(Props[Slave], s"slave_$idx")).toList

  // need their actor path
  val slavePaths = slaveList.map(slaveRef => slaveRef.path.toString)

  // 3.1 in the code
  val groupMaster = system.actorOf(RoundRobinGroup(slavePaths).props())

  //  for (idx <- 1 to 10) {
  //    groupMaster ! s"[$idx] Hello from the world!"
  //  }

  // 3.2 from configuration
  val groupMaster2 = system.actorOf(FromConfig.props(), "groupMaster2")

  for (idx <- 1 to 10) {
    groupMaster2 ! s"[$idx] Hello from the world!"
  }

  /**
   * Handling the special messages
   */

  // this message will send to all slaves regardless of the strategy pattern
  groupMaster2 ! Broadcast("Hello everyone!")

  // PoisonPill and Kill are NOT routed
  // AddRoutee, Remove, Get handled only by the routing actor


}
